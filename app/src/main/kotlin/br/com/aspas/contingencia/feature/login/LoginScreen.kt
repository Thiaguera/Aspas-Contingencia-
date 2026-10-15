package br.com.aspas.contingencia.feature.login
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.common.PinHasher
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.UserEntity
import br.com.aspas.contingencia.domain.model.User
import br.com.aspas.contingencia.domain.repository.UserRepository
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(
private val db: AspasDatabase,
private val userRepository: UserRepository,
private val session: SessionManager
) : ViewModel() {
val users: StateFlow<List<User>> = userRepository.observeActive()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
private val _result = MutableSharedFlow<String?>()
val result: SharedFlow<String?> = _result.asSharedFlow()
fun attempt(userId: String, pin: String) {
viewModelScope.launch {
val user = db.userDao().getById(userId)
val ok = user != null && PinHasher.verify(pin, user.pinSalt, user.pinHash)
if (ok && user != null) session.login(user.userId, user.name, user.role)
_result.emit(if (ok) null else "PIN incorreto")
}
}
fun seedAdminIfMissing() {
viewModelScope.launch {
if (db.userDao().listActive().isEmpty()) {
val salt = PinHasher.newSalt()
db.userDao().upsert(UserEntity("usr-admin", "Admin", "manager",
PinHasher.hash("0000", salt), salt, true, System.currentTimeMillis()))
}
}
}
}
@Composable
fun LoginScreen(onSuccess: () -> Unit, vm: LoginViewModel = hiltViewModel()) {
LaunchedEffect(Unit) { vm.seedAdminIfMissing() }
val users by vm.users.collectAsState()
var selected by remember { mutableStateOf<User?>(null) }
var pin by remember { mutableStateOf("") }
var error by remember { mutableStateOf<String?>(null) }
LaunchedEffect(Unit) {
vm.result.collect { if (it == null) onSuccess() else error = it }
}
Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
Text("ASPAS Contingência", style = MaterialTheme.typography.headlineMedium)
Spacer(Modifier.height(24.dp))
if (selected == null) {
Text("Quem está operando?", style = MaterialTheme.typography.titleMedium)
Spacer(Modifier.height(12.dp))
LazyColumn(Modifier.weight(1f)) {
items(users, key = { it.userId }) { u ->
Card(onClick = { selected = u },
modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
ListItem(headlineContent = { Text(u.name) },
supportingContent = { Text(u.role) })
}
}
}
} else {
Text("Olá, ${selected!!.name}", style = MaterialTheme.typography.titleLarge)
Spacer(Modifier.height(12.dp))
OutlinedTextField(value = pin,
onValueChange = { pin = it.filter(Char::isDigit).take(6) },
label = { Text("PIN") },
visualTransformation = PasswordVisualTransformation(),
singleLine = true)
error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
Spacer(Modifier.height(12.dp))
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
OutlinedButton(onClick = { selected = null; pin = ""; error = null }) { Text("Voltar") }
Button(onClick = { vm.attempt(selected!!.userId, pin) }, enabled = pin.length >= 4) {
Text("Entrar") }
}
}
}
}
