package br.com.aspas.contingencia.feature.management
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.aspas.contingencia.common.PinHasher
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.backup.EncryptedBackup
import br.com.aspas.contingencia.data.backup.PortableBackupService
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.UserEntity
import br.com.aspas.contingencia.data.projection.ProjectionEngine
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.repository.EventRepository
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
data class ManagementReport(
val sessionId: String? = null,
val openedAt: Long? = null,
val orderCount: Int = 0,
val grossOrders: BigDecimal = BigDecimal.ZERO,
val payments: Map<String, BigDecimal> = emptyMap(),
val received: BigDecimal = BigDecimal.ZERO
)
@HiltViewModel
class ManagementViewModel @Inject constructor(
@ApplicationContext private val context: Context,
private val db: AspasDatabase,
private val events: EventRepository,
private val projection: ProjectionEngine,
private val session: SessionManager,
private val json: Json
) : ViewModel() {
private val _message = MutableStateFlow<String?>(null)
val message: StateFlow<String?> = _message.asStateFlow()
private val _busy = MutableStateFlow(false)
val busy: StateFlow<Boolean> = _busy.asStateFlow()
// Serializes management mutations without nesting ProjectionEngine's transaction/mutex.
private val actionMutex = Mutex()
val users = db.userDao().observeActive().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
val activeCash = db.cashSessionDao().observeActive().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
private val _report = MutableStateFlow(ManagementReport())
val report: StateFlow<ManagementReport> = _report.asStateFlow()
private fun requireManager(): SessionManager.Session {
val current = session.session.value ?: throw SecurityException("Sessão expirada")
check(current.role.equals("manager", ignoreCase = true)) { "Apenas gerente pode executar esta ação" }
return current
}
private suspend fun appendAndProject(type: String, payload: String, userId: String): String {
val event = events.append(type, payload, userId)
// Applying the just-written envelope makes the UI wait for a durable projection.
projection.apply(event)
return event.eventId
}
private suspend fun audit(actor: SessionManager.Session, action: String, targetType: String? = null, targetId: String? = null, details: Map<String, String> = emptyMap()) {
appendAndProject(EventType.AUDIT_ACTION_RECORDED, json.encodeToString(AuditActionRecordedPayload(
action, actor.userId, session.currentDeviceId(), targetType, targetId, details
)), actor.userId)
}
fun createOperator(name: String, pin: String) = runAction {
val actor = requireManager()
require(name.trim().length >= 2) { "Informe o nome do operador" }
require(pin.matches(Regex("\\d{4,6}"))) { "O PIN deve ter de 4 a 6 dígitos" }
val userId = Uuid.new()
val salt = PinHasher.newSalt()
val hash = PinHasher.hash(pin, salt)
appendAndProject(EventType.USER_CREATED, json.encodeToString(UserCreatedPayload(userId, name.trim(), "operator", hash, salt)), actor.userId)
audit(actor, "user_created", "user", userId, mapOf("role" to "operator"))
"Operador criado."
}
fun deactivateOperator(userId: String) = runAction {
val actor = requireManager()
require(userId != actor.userId) { "Não é permitido desativar sua própria sessão" }
val user = db.userDao().getById(userId) ?: error("Operador não encontrado")
require(user.role.equals("operator", true)) { "Somente operadores podem ser desativados nesta tela" }
appendAndProject(EventType.USER_DEACTIVATED, json.encodeToString(UserDeactivatedPayload(userId, actor.userId)), actor.userId)
audit(actor, "user_deactivated", "user", userId)
"Operador desativado."
}
fun openCash(amount: String) = runAction {
val actor = requireManager()
require(db.cashSessionDao().active() == null) { "Já existe um caixa aberto" }
val opening = money(amount)
require(opening >= BigDecimal.ZERO) { "A abertura não pode ser negativa" }
val id = Uuid.new()
appendAndProject(EventType.CASH_SESSION_OPENED, json.encodeToString(CashSessionOpenedPayload(id, actor.userId, System.currentTimeMillis(), opening.toDouble())), actor.userId)
audit(actor, "cash_opened", "cash_session", id, mapOf("opening" to opening.toPlainString()))
"Caixa aberto."
}
fun registerMovement(kind: String, amount: String, reason: String) = runAction {
val actor = requireManager()
val cash = db.cashSessionDao().active() ?: error("Não há caixa aberto")
require(kind == "in" || kind == "out") { "Tipo de movimentação inválido" }
val value = money(amount)
require(value > BigDecimal.ZERO) { "Informe um valor maior que zero" }
require(reason.trim().isNotEmpty()) { "Informe o motivo da movimentação" }
val movementId = Uuid.new()
appendAndProject(EventType.CASH_MOVEMENT, json.encodeToString(CashMovementPayload(movementId, cash.sessionId, kind, value.toDouble(), reason.trim(), actor.userId)), actor.userId)
audit(actor, "cash_movement", "cash_movement", movementId, mapOf("kind" to kind, "amount" to value.toPlainString()))
"Movimentação registrada."
}
fun closeCash(counted: String, notes: String) = runAction {
val actor = requireManager()
val cash = db.cashSessionDao().active() ?: error("Não há caixa aberto")
val closing = money(counted)
require(closing >= BigDecimal.ZERO) { "A conferência não pode ser negativa" }
val expected = expectedCash(cash.sessionId, cash.openedAt, cash.openingAmount)
val difference = closing.subtract(expected)
appendAndProject(EventType.CASH_SESSION_CLOSED, json.encodeToString(CashSessionClosedPayload(
cash.sessionId, actor.userId, closing.toDouble(), expected.toDouble(), difference.toDouble(), notes.trim().ifBlank { null }
)), actor.userId)
audit(actor, "cash_closed", "cash_session", cash.sessionId, mapOf("expected" to expected.toPlainString(), "difference" to difference.toPlainString()))
"Caixa fechado. Diferença: ${formatMoney(difference)}"
}
fun refreshReport() = runAction {
val actor = requireManager()
val active = db.cashSessionDao().active() ?: error("Não há caixa aberto para gerar o relatório")
val now = System.currentTimeMillis()
val orders = db.orderDao().listBetween(active.openedAt, now)
val payments = db.paymentDao().listBySessionWindow(active.openedAt, now).filter { !it.reversed }
val grouped = payments.groupBy { it.method }.mapValues { (_, list) -> list.fold(BigDecimal.ZERO) { total, p -> total.add(decimal(p.amount)) } }
_report.value = ManagementReport(active.sessionId, active.openedAt, orders.size, orders.fold(BigDecimal.ZERO) { total, o -> total.add(decimal(o.total)) }, grouped, grouped.values.fold(BigDecimal.ZERO, BigDecimal::add))
audit(actor, "report_viewed", "cash_session", active.sessionId, mapOf("from" to active.openedAt.toString(), "to" to now.toString()))
"Relatório atualizado."
}
fun exportBackup(destination: Uri, password: CharArray) = runAction {
val actor = requireManager()
require(password.size >= EncryptedBackup.MIN_PASSWORD_CHARS) { "A senha do backup precisa ter pelo menos 12 caracteres" }
val temp = File.createTempFile("aspas-export-", ".aspb", context.cacheDir)
val passphrase = databasePassphrase()
try {
PortableBackupService(context, db).export(temp, password, passphrase)
require(temp.length() in 1..EncryptedBackup.MAX_ARCHIVE_BYTES) { "Backup excede o tamanho máximo permitido" }
context.contentResolver.openOutputStream(destination, "w")?.use { output -> temp.inputStream().use { it.copyTo(output) } }
?: error("Não foi possível gravar no local selecionado")
audit(actor, "backup_exported", details = mapOf("bytes" to temp.length().toString()))
"Backup exportado com sucesso."
} finally { password.fill('\u0000'); passphrase.fill(0); temp.delete() }
}
fun validateBackup(source: Uri, password: CharArray) = runAction {
val actor = requireManager()
require(password.size >= EncryptedBackup.MIN_PASSWORD_CHARS) { "A senha do backup precisa ter pelo menos 12 caracteres" }
val archive = File.createTempFile("aspas-validate-", ".aspb", context.cacheDir)
val isolatedName = "backup-validation-${Uuid.new()}.db"
try {
context.contentResolver.openInputStream(source)?.use { input ->
archive.outputStream().use { output -> copyWithLimit(input, output, EncryptedBackup.MAX_ARCHIVE_BYTES) }
}
?: error("Não foi possível ler o arquivo selecionado")
// Explicit validation only restores to a unique isolated database; it never touches the active DB.
val verification = PortableBackupService(context, db).restoreIntoNewDatabase(archive, password, isolatedName)
audit(actor, "backup_validated", details = mapOf("events" to verification.eventCount.toString(), "schema" to verification.schemaVersion.toString()))
"Backup válido: ${verification.eventCount} eventos, esquema ${verification.schemaVersion}."
} finally {
password.fill('\u0000'); archive.delete()
val isolated = context.getDatabasePath(isolatedName)
listOf(isolated, File(isolated.path + "-wal"), File(isolated.path + "-shm"), File(isolated.path + "-journal")).forEach(File::delete)
}
}
private suspend fun expectedCash(sessionId: String, openedAt: Long, opening: Double): BigDecimal {
val cashPayments = db.paymentDao().listBySessionWindow(openedAt, System.currentTimeMillis())
.filter { !it.reversed && it.method.equals("cash", true) }
.fold(BigDecimal.ZERO) { total, payment -> total.add(decimal(payment.amount)) }
val movements = db.cashMovementDao().listBySession(sessionId).fold(BigDecimal.ZERO) { total, movement ->
val value = decimal(movement.amount)
total.add(if (movement.kind == "in") value else value.negate())
}
return decimal(opening).add(cashPayments).add(movements).setScale(2, RoundingMode.HALF_UP)
}
private fun databasePassphrase(): ByteArray {
val master = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
val prefs = EncryptedSharedPreferences.create(context, "aspas_secrets", master,
EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
val hex = checkNotNull(prefs.getString("db_passphrase", null)) { "Chave local do banco indisponível" }
require(hex.length in 32..256 && hex.length % 2 == 0 && hex.all { it.digitToIntOrNull(16) != null }) { "Chave local do banco inválida" }
return ByteArray(hex.length / 2) { index -> hex.substring(index * 2, index * 2 + 2).toInt(16).toByte() }
}
private fun money(value: String): BigDecimal {
val normalized = value.trim().let { raw ->
if (raw.contains(',')) raw.replace(".", "").replace(',', '.') else raw
}
return normalized.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
}
private fun decimal(value: Double) = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP)
private fun copyWithLimit(input: java.io.InputStream, output: java.io.OutputStream, limit: Long) {
val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
var copied = 0L
while (true) {
val read = input.read(buffer)
if (read < 0) return
copied += read
require(copied <= limit) { "Backup excede o tamanho máximo permitido" }
output.write(buffer, 0, read)
}
}
private fun runAction(action: suspend () -> String) {
if (!actionMutex.tryLock()) return
_busy.value = true
_message.value = null
viewModelScope.launch {
try { _message.value = withContext(Dispatchers.IO) { action() } }
catch (t: Throwable) { _message.value = t.message ?: "Não foi possível concluir a operação" }
finally { _busy.value = false; actionMutex.unlock() }
}
}
}
