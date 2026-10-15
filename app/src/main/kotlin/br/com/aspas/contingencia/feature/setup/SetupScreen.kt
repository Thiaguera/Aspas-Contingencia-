package br.com.aspas.contingencia.feature.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.data.bootstrap.FirstRunProvisioner
import br.com.aspas.contingencia.data.bootstrap.ProvisioningProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SetupUiState {
    data object Checking : SetupUiState
    data class NeedsTableChoice(val catalogPresent: Boolean, val tablesPresent: Boolean) : SetupUiState
    data class Working(val message: String) : SetupUiState
    data object Ready : SetupUiState
    data class Error(val message: String) : SetupUiState
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val provisioner: FirstRunProvisioner
) : ViewModel() {
    private val _state = MutableStateFlow<SetupUiState>(SetupUiState.Checking)
    val state: StateFlow<SetupUiState> = _state.asStateFlow()

    init { check() }

    fun check() = viewModelScope.launch {
        runCatching { provisioner.snapshot() }
            .onSuccess { snapshot ->
                if (snapshot.tablesPresent) {
                    _state.value = SetupUiState.Working("Preparando catálogo…")
                    _state.value = runCatching { provisioner.initializeCatalogIfMissing() }
                        .fold(onSuccess = { SetupUiState.Ready }, onFailure = { SetupUiState.Error(it.userMessage()) })
                } else {
                    _state.value = SetupUiState.NeedsTableChoice(snapshot.catalogPresent, tablesPresent = false)
                }
            }
            .onFailure { _state.value = SetupUiState.Error(it.userMessage()) }
    }

    fun provision(first: Int, last: Int) = viewModelScope.launch {
        if (first <= 0 || last < first) {
            _state.value = SetupUiState.Error("Informe uma faixa válida de mesas, por exemplo 1 até 20.")
            return@launch
        }
        runCatching {
            provisioner.provision(first..last) { progress ->
                _state.value = SetupUiState.Working(progress.description())
            }
        }.onSuccess { _state.value = SetupUiState.Ready }
            .onFailure { _state.value = SetupUiState.Error(it.userMessage()) }
    }

    private fun ProvisioningProgress.description() = when (this) {
        ProvisioningProgress.Idle -> "Aguardando…"
        ProvisioningProgress.LoadingCatalog -> "Carregando catálogo inicial…"
        ProvisioningProgress.CreatingTables -> "Criando somente as mesas ausentes…"
        is ProvisioningProgress.Complete -> "Configuração concluída"
        is ProvisioningProgress.Failed -> message
    }

    private fun Throwable.userMessage() = message ?: "Não foi possível concluir a configuração. Tente novamente."
}

/** First-run configuration. Navigation owns when this destination is shown. */
@Composable
fun SetupScreen(onReady: () -> Unit, vm: SetupViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var firstText by remember { mutableStateOf("1") }
    var lastText by remember { mutableStateOf("20") }

    LaunchedEffect(state) { if (state is SetupUiState.Ready) onReady() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Configuração inicial", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        when (val current = state) {
            SetupUiState.Checking -> Loading("Verificando dados existentes…")
            is SetupUiState.Working -> Loading(current.message)
            SetupUiState.Ready -> Loading("Configuração concluída…")
            is SetupUiState.Error -> {
                Text(current.message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = vm::check) { Text("Tentar novamente") }
            }
            is SetupUiState.NeedsTableChoice -> {
                Text("Escolha os números das mesas que serão usados neste salão.")
                Text("Sugestão editável: 1 a 20. Nenhuma mesa é criada sem sua confirmação.",
                    style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = firstText,
                        onValueChange = { firstText = it.filter(Char::isDigit) },
                        label = { Text("Primeira mesa") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lastText,
                        onValueChange = { lastText = it.filter(Char::isDigit) },
                        label = { Text("Última mesa") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { vm.provision(firstText.toIntOrNull() ?: 0, lastText.toIntOrNull() ?: -1) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Confirmar e preparar") }
            }
        }
    }
}

@Composable
private fun Loading(message: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CircularProgressIndicator()
        Text(message)
    }
}
