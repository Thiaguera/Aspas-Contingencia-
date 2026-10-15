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
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.projection.ProjectionEngine
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.UserCreatedPayload
import br.com.aspas.contingencia.domain.model.User
import br.com.aspas.contingencia.domain.repository.EventRepository
import br.com.aspas.contingencia.domain.repository.UserRepository
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private sealed interface LoginResult {
    data object Success : LoginResult
    data class Error(val message: String) : LoginResult
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val db: AspasDatabase,
    private val userRepository: UserRepository,
    private val events: EventRepository,
    private val projection: ProjectionEngine,
    private val session: SessionManager,
    private val json: Json
) : ViewModel() {
    val users: StateFlow<List<User>> = userRepository.observeActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()
    // This is based on an explicit DAO read, rather than the initial empty Flow value.
    // It prevents briefly showing enrollment while existing users are still loading.
    private val _needsEnrollment = MutableStateFlow(false)
    val needsEnrollment: StateFlow<Boolean> = _needsEnrollment.asStateFlow()
    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting.asStateFlow()
    // Replay the latest failure so an initialization error is not lost before Compose starts collecting.
    private val _result = MutableSharedFlow<LoginResult>(replay = 1)
    val result: SharedFlow<LoginResult> = _result.asSharedFlow()

    private var wrongAttempts = 0
    private var lockedUntil = 0L

    init {
        viewModelScope.launch {
            // Do not provision or alter existing accounts (including usr-admin).
            try {
                _needsEnrollment.value = db.userDao().listActive().isEmpty()
            } catch (_: Exception) {
                _result.emit(LoginResult.Error("Não foi possível carregar os usuários. Tente novamente."))
            } finally {
                _ready.value = true
            }
        }
    }

    fun attempt(userId: String, pin: String) {
        if (_submitting.value) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (now < lockedUntil) {
                _result.emit(LoginResult.Error("Muitas tentativas. Aguarde ${(lockedUntil - now + 999) / 1_000} segundos."))
                return@launch
            }
            if (lockedUntil != 0L) {
                lockedUntil = 0L
                wrongAttempts = 0
            }
            _submitting.value = true
            try {
                val user = db.userDao().getById(userId)
                val ok = user != null && user.active && PinHasher.verify(pin, user.pinSalt, user.pinHash)
                if (ok) {
                    wrongAttempts = 0
                    session.login(user.userId, user.name, user.role)
                    _result.emit(LoginResult.Success)
                } else {
                    wrongAttempts++
                    if (wrongAttempts >= MAX_WRONG_ATTEMPTS) {
                        lockedUntil = System.currentTimeMillis() + LOCKOUT_MILLIS
                        wrongAttempts = 0
                        _result.emit(LoginResult.Error("Muitas tentativas. Aguarde 30 segundos."))
                    } else {
                        _result.emit(LoginResult.Error("PIN incorreto ou usuário inativo."))
                    }
                }
            } catch (_: Exception) {
                _result.emit(LoginResult.Error("Não foi possível entrar. Tente novamente."))
            } finally {
                _submitting.value = false
            }
        }
    }

    fun enrollFirstManager(name: String, pin: String, confirmation: String) {
        if (_submitting.value) return
        viewModelScope.launch {
            _submitting.value = true
            try {
                require(name.trim().length >= 2) { "Informe o nome do gerente." }
                require(pin.matches(Regex("\\d{4,6}"))) { "O PIN deve ter de 4 a 6 dígitos." }
                require(pin == confirmation) { "A confirmação do PIN não confere." }
                // Re-check at submission time: never overwrite an account that appeared meanwhile.
                require(db.userDao().listActive().isEmpty()) { "Já existe um usuário cadastrado. Entre com ele." }

                val userId = Uuid.new()
                val salt = PinHasher.newSalt()
                val event = events.append(
                    EventType.USER_CREATED,
                    json.encodeToString(UserCreatedPayload(userId, name.trim(), "manager", PinHasher.hash(pin, salt), salt))
                )
                // Login is allowed only after the user projection has committed durably.
                projection.apply(event)
                val manager = db.userDao().getById(userId)
                    ?: error("Não foi possível concluir o cadastro do gerente.")
                check(manager.active && manager.role.equals("manager", ignoreCase = true)) {
                    "Não foi possível concluir o cadastro do gerente."
                }
                session.login(manager.userId, manager.name, manager.role)
                _result.emit(LoginResult.Success)
            } catch (error: Exception) {
                _result.emit(LoginResult.Error(error.message ?: "Não foi possível criar o gerente. Tente novamente."))
            } finally {
                _submitting.value = false
            }
        }
    }

    private companion object {
        const val MAX_WRONG_ATTEMPTS = 5
        const val LOCKOUT_MILLIS = 30_000L
    }
}

@Composable
fun LoginScreen(onSuccess: () -> Unit, vm: LoginViewModel = hiltViewModel()) {
    val users by vm.users.collectAsState()
    val ready by vm.ready.collectAsState()
    val needsEnrollment by vm.needsEnrollment.collectAsState()
    val submitting by vm.submitting.collectAsState()
    var selected by remember { mutableStateOf<User?>(null) }
    var pin by remember { mutableStateOf("") }
    var managerName by remember { mutableStateOf("") }
    var managerPin by remember { mutableStateOf("") }
    var managerPinConfirmation by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.result.collect { result ->
            when (result) {
                LoginResult.Success -> onSuccess()
                is LoginResult.Error -> error = result.message
            }
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ASPAS Contingência", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        when {
            !ready -> CircularProgressIndicator()
            needsEnrollment -> FirstManagerForm(
                name = managerName,
                pin = managerPin,
                confirmation = managerPinConfirmation,
                error = error,
                submitting = submitting,
                onNameChange = { managerName = it; error = null },
                onPinChange = { managerPin = it.filter(Char::isDigit).take(6); error = null },
                onConfirmationChange = { managerPinConfirmation = it.filter(Char::isDigit).take(6); error = null },
                onSubmit = { vm.enrollFirstManager(managerName, managerPin, managerPinConfirmation) }
            )
            selected == null -> {
                Text("Quem está operando?", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                LazyColumn(Modifier.weight(1f)) {
                    items(users, key = { it.userId }) { user ->
                        Card(
                            onClick = { selected = user; error = null },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            ListItem(headlineContent = { Text(user.name) }, supportingContent = { Text(user.role) })
                        }
                    }
                }
            }
            else -> {
                Text("Olá, ${selected!!.name}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter(Char::isDigit).take(6); error = null },
                    label = { Text("PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !submitting
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { selected = null; pin = ""; error = null }, enabled = !submitting) { Text("Voltar") }
                    Button(onClick = { vm.attempt(selected!!.userId, pin) }, enabled = pin.length >= 4 && !submitting) {
                        if (submitting) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Entrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun FirstManagerForm(
    name: String,
    pin: String,
    confirmation: String,
    error: String?,
    submitting: Boolean,
    onNameChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onConfirmationChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text("Criar gerente", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    Text("Cadastre o primeiro gerente para começar.")
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(name, onNameChange, label = { Text("Nome do gerente") }, singleLine = true, enabled = !submitting)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(pin, onPinChange, label = { Text("PIN (4 a 6 dígitos)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, enabled = !submitting)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(confirmation, onConfirmationChange, label = { Text("Confirmar PIN") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, enabled = !submitting)
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    Spacer(Modifier.height(16.dp))
    Button(onClick = onSubmit, enabled = name.trim().length >= 2 && pin.length in 4..6 && confirmation.length in 4..6 && !submitting) {
        if (submitting) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Criar gerente")
    }
}
