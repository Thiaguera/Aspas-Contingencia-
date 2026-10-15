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
@HiltViewModel
class CheckoutViewModel @Inject constructor(
private val orderRepository: OrderRepository,
private val closeTableUseCase: CloseTableUseCase,
private val session: SessionManager,
savedStateHandle: SavedStateHandle
) : ViewModel() {
val tableId: String = savedStateHandle["tableId"] ?: ""
private val orderId: String = savedStateHandle["orderId"] ?: ""
val order: StateFlow<Order?> = orderRepository.observeById(orderId)
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
private val _payments = MutableStateFlow<List<Triple<String, Double, String?>>>(emptyList())
val payments: StateFlow<List<Triple<String, Double, String?>>> = _payments.asStateFlow()
private val _event = MutableSharedFlow<String>()
val event: SharedFlow<String> = _event.asSharedFlow()
fun addPayment(method: String, amount: Double) {
_payments.value = _payments.value + Triple(method, amount, null)
}
fun removePayment(i: Int) {
_payments.value = _payments.value.filterIndexed { idx, _ -> idx != i }
}
fun confirm() {
val uid = session.currentUserId() ?: return
val o = order.value ?: return
viewModelScope.launch {
closeTableUseCase(tableId, o, _payments.value, uid)
.onSuccess { _event.emit("fechado") }
.onFailure { _event.emit("erro: ${it.message}") }
}
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBack: () -> Unit, onClosed: () -> Unit,
vm: CheckoutViewModel = hiltViewModel()) {
val order by vm.order.collectAsState()
val payments by vm.payments.collectAsState()
val snack = remember { SnackbarHostState() }
var showPayment by remember { mutableStateOf(false) }
LaunchedEffect(Unit) {
vm.event.collect { if (it == "fechado") onClosed() else snack.showSnackbar(it) }
}
Scaffold(
topBar = { TopAppBar(title = { Text("Fechar mesa") },
navigationIcon = { IconButton(onClick = onBack) {
Icon(Icons.Default.ArrowBack, null) } }) },
snackbarHost = { SnackbarHost(snack) }
) { padding ->
Column(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {
order?.let {
Text("Subtotal: R %.2f".format(it.subtotal))
Text("Taxa 10%: R$ %.2f".format(it.serviceFee))
Text("Total: R$ %.2f".format(it.total),
style = MaterialTheme.typography.titleLarge)
}
Spacer(Modifier.height(12.dp))
Text("Pagamentos", style = MaterialTheme.typography.titleSmall)
payments.forEachIndexed { i, p ->
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("${p.first} — R %.2f".format(p.second))
TextButton(onClick = { vm.removePayment(i) }) { Text("x") } }
}
Spacer(Modifier.weight(1f))
Button(onClick = { showPayment = true }, modifier = Modifier.fillMaxWidth()) {
Text("+ Pagamento") }
Spacer(Modifier.height(8.dp))
Button(onClick = { vm.confirm() }, modifier = Modifier.fillMaxWidth(),
enabled = payments.isNotEmpty()) { Text("Confirmar fechamento") }
}
}
if (showPayment) {
var method by remember { mutableStateOf("cash") }
var amount by remember { mutableStateOf("") }
AlertDialog(onDismissRequest = { showPayment = false },
title = { Text("Novo pagamento") },
text = {
Column {
listOf("cash", "credit", "debit", "pix", "voucher").forEach { m ->
Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
RadioButton(selected = method == m, onClick = { method = m })
Text(m)
} }
OutlinedTextField(value = amount, onValueChange = { amount = it },
label = { Text("Valor") })
}
},
confirmButton = {
TextButton(onClick = {
val v = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
if (v > 0) vm.addPayment(method, v); showPayment = false
}) { Text("Adicionar") } },
dismissButton = { TextButton(onClick = { showPayment = false }) { Text("Cancelar") } })
}
}
