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

private @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(onBack: () -> Unit, vm: ManagementViewModel = hiltViewModel()) {
    val users by vm.users.collectAsState()
    val cash by vm.activeCash.collectAsState()
    val report by vm.report.collectAsState()
    val busy by vm.busy.collectAsState()
    val message by vm.message.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var backupPassword by remember { mutableStateOf("") }
    var pendingExport by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null && pendingExport) vm.exportBackup(uri, backupPassword.toCharArray())
        pendingExport = false; backupPassword = ""
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.validateBackup(uri, backupPassword.toCharArray())
        backupPassword = ""
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Gestão") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Voltar") } }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tab) { listOf("Equipe", "Caixa", "Relatórios", "Backup").forEachIndexed { index, title -> Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) }) } }
            message?.let { Text(it, Modifier.padding(16.dp), color = if (it.startsWith("Não") || it.startsWith("Apenas") || it.startsWith("PIN")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
            when (tab) {
                0 -> TeamTab(users, busy, vm)
                1 -> CashTab(cash != null, busy, vm)
                2 -> ReportsTab(report, busy, vm)
                else -> BackupTab(backupPassword, { backupPassword = it }, busy, onExport = { pendingExport = true; exportLauncher.launch("aspas-backup-${System.currentTimeMillis()}.aspb") }, onValidate = { importLauncher.launch(arrayOf("application/octet-stream", "application/*")) })
            }
        }
    }
}

@Composable private fun TeamTab(users: List<br.com.aspas.contingencia.data.db.entity.UserEntity>, busy: Boolean, vm: ManagementViewModel) {
    var name by remember { mutableStateOf("") }; var pin by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Novo operador", style = MaterialTheme.typography.titleMedium) }
        item { OutlinedTextField(name, { name = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(pin, { pin = it.filter(Char::isDigit).take(6) }, label = { Text("PIN (4 a 6 dígitos)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { Button(onClick = { vm.createOperator(name, pin); name = ""; pin = "" }, enabled = !busy) { Text("Criar operador") } }
        item { HorizontalDivider(); Text("Operadores ativos", style = MaterialTheme.typography.titleMedium) }
        items(users, key = { it.userId }) { user -> ListItem(headlineContent = { Text(user.name) }, supportingContent = { Text(user.role) }, trailingContent = { if (user.role.equals("operator", true)) TextButton(onClick = { vm.deactivateOperator(user.userId) }, enabled = !busy) { Text("Desativar") } }); HorizontalDivider() }
    }
}

@Composable private fun CashTab(open: Boolean, busy: Boolean, vm: ManagementViewModel) {
    var opening by remember { mutableStateOf("") }; var movement by remember { mutableStateOf("") }; var reason by remember { mutableStateOf("") }; var counted by remember { mutableStateOf("") }; var notes by remember { mutableStateOf("") }; var isIn by remember { mutableStateOf(true) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text(if (open) "Caixa aberto" else "Caixa fechado", style = MaterialTheme.typography.titleMedium) }
        if (!open) { item { OutlinedTextField(opening, { opening = it }, label = { Text("Fundo de troco") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }; item { Button(onClick = { vm.openCash(opening) }, enabled = !busy) { Text("Abrir caixa") } } }
        else {
            item { Text("Movimentação") }; item { Row { FilterChip(selected = isIn, onClick = { isIn = true }, label = { Text("Entrada") }); Spacer(Modifier.width(8.dp)); FilterChip(selected = !isIn, onClick = { isIn = false }, label = { Text("Sangria/saída") }) } }
            item { OutlinedTextField(movement, { movement = it }, label = { Text("Valor") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }; item { OutlinedTextField(reason, { reason = it }, label = { Text("Motivo") }, modifier = Modifier.fillMaxWidth()) }; item { Button(onClick = { vm.registerMovement(if (isIn) "in" else "out", movement, reason); movement = ""; reason = "" }, enabled = !busy) { Text("Registrar movimentação") } }
            item { HorizontalDivider(); Text("Fechamento", style = MaterialTheme.typography.titleMedium) }; item { OutlinedTextField(counted, { counted = it }, label = { Text("Valor conferido") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }; item { OutlinedTextField(notes, { notes = it }, label = { Text("Observações (opcional)") }, modifier = Modifier.fillMaxWidth()) }; item { Button(onClick = { vm.closeCash(counted, notes) }, enabled = !busy) { Text("Fechar caixa") } }
        }
    }
}

@Composable private fun ReportsTab(report: ManagementReport, busy: Boolean, vm: ManagementViewModel) {
    val currency = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Relatório do caixa aberto atual", style = MaterialTheme.typography.titleMedium) }; item { Button(onClick = vm::refreshReport, enabled = !busy) { Text("Atualizar") } }
        if (report.sessionId == null) { item { Text("Abra um caixa e atualize para consultar o período.") } }
        else { item { Text("Pedidos: ${report.orderCount}") }; item { Text("Total dos pedidos: ${currency.format(report.grossOrders)}") }; item { Text("Pagamentos não estornados: ${currency.format(report.received)}") }; item { Text("Por meio", style = MaterialTheme.typography.titleMedium) } }
        items(report.payments.entries.toList(), key = { it.key }) { (method, amount) -> ListItem(headlineContent = { Text(method) }, trailingContent = { Text(currency.format(amount)) }); HorizontalDivider() }
    }
}

@Composable private fun BackupTab(password: String, onPassword: (String) -> Unit, busy: Boolean, onExport: () -> Unit, onValidate: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Backup portátil", style = MaterialTheme.typography.titleMedium)
        Text("Use uma senha exclusiva com ao menos 12 caracteres. A validação abre somente uma cópia isolada; nunca restaura nem substitui o banco em uso.")
        OutlinedTextField(password, onPassword, label = { Text("Senha do backup") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(onClick = onExport, enabled = !busy && password.length >= 12) { Text("Exportar backup") }
        OutlinedButton(onClick = onValidate, enabled = !busy && password.length >= 12) { Text("Selecionar e validar backup") }
    }
}

internal fun formatMoney(value: BigDecimal): String = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
