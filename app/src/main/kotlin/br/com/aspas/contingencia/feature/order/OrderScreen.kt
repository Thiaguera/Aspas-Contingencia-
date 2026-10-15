
package br.com.aspas.contingencia.feature.order
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.CatalogItem
import br.com.aspas.contingencia.domain.model.Order
import br.com.aspas.contingencia.domain.model.OrderItem
import br.com.aspas.contingencia.domain.repository.CatalogRepository
import br.com.aspas.contingencia.domain.repository.OrderRepository
import br.com.aspas.contingencia.domain.usecase.AddOrderItemUseCase
import br.com.aspas.contingencia.domain.usecase.AddOrderNoteUseCase
import br.com.aspas.contingencia.domain.usecase.CancelOrderItemUseCase
import br.com.aspas.contingencia.domain.usecase.SendToProductionUseCase
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
private val orderRepository: OrderRepository,
private val catalogRepository: CatalogRepository,
private val addOrderItem: AddOrderItemUseCase,
private val cancelOrderItem: CancelOrderItemUseCase,
private val sendToProduction: SendToProductionUseCase,
private val addOrderNote: AddOrderNoteUseCase,
private val session: SessionManager,
savedStateHandle: SavedStateHandle
) : ViewModel() {
val tableId: String = savedStateHandle["tableId"] ?: ""
private val _order = MutableStateFlow<Order?>(null)
val order: StateFlow<Order?> = _order.asStateFlow()
val items: StateFlow<List<OrderItem>> = flow {
val o = orderRepository.activeByTable(tableId)
_order.value = o
if (o != null) emitAll(orderRepository.observeItems(o.orderId))
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
val catalog: StateFlow<List<CatalogItem>> = catalogRepository.observeAvailableItems()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
private val _event = MutableSharedFlow<String>()
val event: SharedFlow<String> = _event.asSharedFlow()
fun addItem(item: CatalogItem) {
val o = _order.value ?: return
val uid = session.currentUserId() ?: return
viewModelScope.launch {
addOrderItem(o.orderId, item.itemId, item.name, item.price, userId = uid)
.onSuccess { _event.emit("+ ${item.name}") }
.onFailure { _event.emit("erro") }
}
}
fun send(item: OrderItem) {
viewModelScope.launch {
sendToProduction(item)
.onSuccess { _event.emit("$it fichas enviadas") }
.onFailure { _event.emit("erro") }
}
}
fun cancel(item: OrderItem) {
val uid = session.currentUserId() ?: return
viewModelScope.launch {
cancelOrderItem(item, "cancelado pelo operador", uid)
.onSuccess { _event.emit("cancelado") }
.onFailure { _event.emit("erro") }
}
}
fun setNote(note: String) {
val o = _order.value ?: return
val uid = session.currentUserId() ?: return
viewModelScope.launch { addOrderNote(o.orderId, note, uid) }
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(onBack: () -> Unit, onCheckout: (String, String) -> Unit,
vm: OrderViewModel = hiltViewModel()) {
val order by vm.order.collectAsState()
val items by vm.items.collectAsState()
val catalog by vm.catalog.collectAsState()
val snack = remember { SnackbarHostState() }
var showCatalog by remember { mutableStateOf(false) }
var editingNote by remember { mutableStateOf(false) }
var noteText by remember { mutableStateOf("") }
LaunchedEffect(Unit) { vm.event.collect { snack.showSnackbar(it) } }
LaunchedEffect(order?.notes) { noteText = order?.notes ?: "" }
Scaffold(
topBar = {
TopAppBar(
title = { Text("Mesa") },
navigationIcon = { IconButton(onClick = onBack) {
Icon(Icons.Default.ArrowBack, null) } })
},
snackbarHost = { SnackbarHost(snack) },
floatingActionButton = {
FloatingActionButton(onClick = { showCatalog = true }) {
Icon(Icons.Default.Add, "Adicionar")
}
}
) { padding ->
Column(Modifier.padding(padding).fillMaxSize()) {
Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
Row(Modifier.fillMaxWidth().padding(12.dp),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically) {
Text(order?.notes?.takeIf { it.isNotBlank() }
?: "Sem observação da mesa",
style = MaterialTheme.typography.bodyMedium)
TextButton(onClick = {
if (editingNote) { vm.setNote(noteText); editingNote = false }
else editingNote = true
}) { Text(if (editingNote) "Salvar" else "Editar") }
}
}
if (editingNote) {
OutlinedTextField(value = noteText, onValueChange = { noteText = it.take(280) },
modifier = Modifier.fillMaxWidth().padding(12.dp),
placeholder = { Text("Ex.: aniversário, alergia…") }, minLines = 2)
}
LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
items(items, key = { it.itemId }) { it ->
ListItem(
headlineContent = { Text("${it.quantity}x ${it.nameSnapshot}") },
supportingContent = { Text("R$ %.2f".format(it.priceSnapshot * it.quantity)) },
trailingContent = {
Row {
IconButton(onClick = { vm.send(it) }) {
Icon(Icons.Default.Send, "Enviar") }
TextButton(onClick = { vm.cancel(it) }) { Text("X") }
}
})
}
}
order?.let {
Card(Modifier.fillMaxWidth().padding(12.dp)) {
Column(Modifier.padding(12.dp)) {
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("Subtotal"); Text("R %.2f".format(it.subtotal)) }
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("Taxa 10%"); Text("R$ %.2f".format(it.serviceFee)) }
HorizontalDivider()
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("Total", style = MaterialTheme.typography.titleLarge)
Text("R$ %.2f".format(it.total), style = MaterialTheme.typography.titleLarge) }
Spacer(Modifier.height(8.dp))
Button(onClick = { onCheckout(vm.tableId, it.orderId) },
modifier = Modifier.fillMaxWidth()) { Text("Fechar") }
}
}
}
}
}
if (showCatalog) {
ModalBottomSheet(onDismissRequest = { showCatalog = false }) {
LazyColumn(Modifier.fillMaxWidth().padding(12.dp)) {
items(catalog, key = { it.itemId }) { cat ->
ListItem(
headlineContent = { Text(cat.name) },
supportingContent = { Text("R$ %.2f".format(cat.price)) },
modifier = Modifier.clickable {
vm.addItem(cat); showCatalog = false })
HorizontalDivider()
}
}
}
}
}
