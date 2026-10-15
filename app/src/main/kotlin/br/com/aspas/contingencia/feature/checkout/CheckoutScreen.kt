package br.com.aspas.contingencia.feature.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.Order
import br.com.aspas.contingencia.domain.repository.OrderRepository
import br.com.aspas.contingencia.domain.usecase.CloseTableUseCase
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CheckoutUiEvent { data object Closed : CheckoutUiEvent; data class Error(val message: String) : CheckoutUiEvent }

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository, private val closeTableUseCase: CloseTableUseCase,
    private val session: SessionManager, savedStateHandle: SavedStateHandle
) : ViewModel() {
    val tableId: String = savedStateHandle["tableId"] ?: ""
    private val orderId: String = savedStateHandle["orderId"] ?: ""
    val order: StateFlow<Order?> = orderRepository.observeById(orderId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    private val _payments = MutableStateFlow<List<Triple<String, Double, String?>>>(emptyList())
    val payments: StateFlow<List<Triple<String, Double, String?>>> = _payments.asStateFlow()
    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting.asStateFlow()
    private val _event = MutableSharedFlow<CheckoutUiEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<CheckoutUiEvent> = _event.asSharedFlow()
    fun addPayment(method: String, amount: Double) { if (!_submitting.value && amount > 0.0 && amount.isFinite()) _payments.value += Triple(method, amount, null) }
    fun removePayment(index: Int) { if (!_submitting.value) _payments.value = _payments.value.filterIndexed { i, _ -> i != index } }
    fun confirm() {
        if (_submitting.value) return
        val uid = session.currentUserId() ?: run { _event.tryEmit(CheckoutUiEvent.Error("Sessão expirada")); return }
        val current = order.value ?: run { _event.tryEmit(CheckoutUiEvent.Error("Comanda indisponível")); return }
        _submitting.value = true
        viewModelScope.launch {
            closeTableUseCase(tableId, current, _payments.value, uid)
                .onSuccess { _event.emit(CheckoutUiEvent.Closed) }
                .onFailure { _event.emit(CheckoutUiEvent.Error(it.message ?: "Não foi possível fechar a mesa")) }
            _submitting.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBack: () -> Unit, onClosed: () -> Unit, vm: CheckoutViewModel = hiltViewModel()) {
    val order by vm.order.collectAsState(); val payments by vm.payments.collectAsState(); val submitting by vm.submitting.collectAsState()
    val snack = remember { SnackbarHostState() }; var showPayment by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.event.collect { when (it) { CheckoutUiEvent.Closed -> onClosed(); is CheckoutUiEvent.Error -> snack.showSnackbar(it.message) } } }
    Scaffold(topBar = { TopAppBar(title = { Text("Fechar mesa") }, navigationIcon = { IconButton(onClick = onBack, enabled = !submitting) { Icon(Icons.Default.ArrowBack, null) } }) }, snackbarHost = { SnackbarHost(snack) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {
            order?.let { Text("Subtotal: R$ %.2f".format(it.subtotal)); Text("Taxa 10%%: R$ %.2f".format(it.serviceFee)); Text("Total: R$ %.2f".format(it.total), style = MaterialTheme.typography.titleLarge) }
            Spacer(Modifier.height(12.dp)); Text("Pagamentos", style = MaterialTheme.typography.titleSmall)
            payments.forEachIndexed { index, payment -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${payment.first} — R$ %.2f".format(payment.second)); TextButton(onClick = { vm.removePayment(index) }, enabled = !submitting) { Text("x") } } }
            Spacer(Modifier.weight(1f)); Button(onClick = { showPayment = true }, Modifier.fillMaxWidth(), enabled = !submitting) { Text("+ Pagamento") }; Spacer(Modifier.height(8.dp)); Button(onClick = vm::confirm, Modifier.fillMaxWidth(), enabled = payments.isNotEmpty() && !submitting) { if (submitting) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Confirmar fechamento") }
        }
    }
    if (showPayment) { var method by remember { mutableStateOf("cash") }; var amount by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = { showPayment = false }, title = { Text("Novo pagamento") }, text = { Column { listOf("cash", "credit", "debit", "pix", "voucher").forEach { option -> Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { RadioButton(method == option, { method = option }); Text(option) } }; OutlinedTextField(amount, { amount = it }, label = { Text("Valor") }) } }, confirmButton = { TextButton(onClick = { amount.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0.0 && it.isFinite() }?.let { vm.addPayment(method, it); showPayment = false } }) { Text("Adicionar") } }, dismissButton = { TextButton(onClick = { showPayment = false }) { Text("Cancelar") } }) }
}
