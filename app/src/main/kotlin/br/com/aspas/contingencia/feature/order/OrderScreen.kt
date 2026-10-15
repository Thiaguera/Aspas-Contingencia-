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
    private val orderRepository: OrderRepository, private val catalogRepository: CatalogRepository,
    private val addOrderItem: AddOrderItemUseCase, private val cancelOrderItem: CancelOrderItemUseCase,
    private val sendToProduction: SendToProductionUseCase, private val addOrderNote: AddOrderNoteUseCase,
    private val session: SessionManager, savedStateHandle: SavedStateHandle
) : ViewModel() {
    val tableId: String = savedStateHandle["tableId"] ?: ""
    val order: StateFlow<Order?> = orderRepository.observeAllOpen().map { orders ->
        orders.firstOrNull { it.tableId == tableId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val items: StateFlow<List<OrderItem>> = order.filterNotNull().map { it.orderId }.distinctUntilChanged()
        .flatMapLatest(orderRepository::observeItems)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val catalog: StateFlow<List<CatalogItem>> = catalogRepository.observeAvailableItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val busyItems = MutableStateFlow<Set<String>>(emptySet())
    private val _event = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val event: SharedFlow<String> = _event.asSharedFlow()
    fun isBusy(itemId: String) = itemId in busyItems.value

    fun addItem(item: CatalogItem, quantity: Int, weightG: Int?, note: String) {
        val current = order.value ?: return
        if (quantity < 1 || item.itemId in busyItems.value) return
        val uid = session.currentUserId() ?: run { _event.tryEmit("Sessão expirada"); return }
        busyItems.value = busyItems.value + item.itemId
        viewModelScope.launch {
            val unitPrice = if (item.pricingMode == "per_100g") {
                if (weightG == null || weightG <= 0) {
                    _event.emit("Informe um peso válido")
                    busyItems.value = busyItems.value - item.itemId
                    return@launch
                }
                item.price * weightG / 100.0
            } else item.price
            addOrderItem(current.orderId, item.itemId, item.name, unitPrice, quantity = quantity,
                weightG = weightG, modifiers = note.takeIf { it.isNotBlank() }?.let { mapOf("observacao" to it) } ?: emptyMap(), userId = uid)
                .onSuccess { _event.emit("+ ${item.name}") }
                .onFailure { _event.emit(it.message ?: "Não foi possível adicionar o item") }
            busyItems.value = busyItems.value - item.itemId
        }
    }
    fun send(item: OrderItem) = itemAction(item.itemId) { sendToProduction(item).map { "$it fichas enviadas" } }
    fun cancel(item: OrderItem) {
        val uid = session.currentUserId() ?: return
        itemAction(item.itemId) { cancelOrderItem(item, "cancelado pelo operador", uid).map { "Cancelado" } }
    }
    private fun itemAction(itemId: String, action: suspend () -> Result<String>) {
        if (itemId in busyItems.value) return
        busyItems.value = busyItems.value + itemId
        viewModelScope.launch { action().onSuccess { _event.emit(it) }.onFailure { _event.emit(it.message ?: "Operação não concluída") }; busyItems.value = busyItems.value - itemId }
    }
    fun setNote(note: String) {
        val current = order.value ?: return
        val uid = session.currentUserId() ?: return
        viewModelScope.launch { addOrderNote(current.orderId, note, uid).onFailure { _event.emit(it.message ?: "Não foi possível salvar a observação") } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(onBack: () -> Unit, onCheckout: (String, String) -> Unit, vm: OrderViewModel = hiltViewModel()) {
    val order by vm.order.collectAsState(); val items by vm.items.collectAsState(); val catalog by vm.catalog.collectAsState()
    val snack = remember { SnackbarHostState() }; var showCatalog by remember { mutableStateOf(false) }; var editingNote by remember { mutableStateOf(false) }; var noteText by remember { mutableStateOf("") }; var selected by remember { mutableStateOf<CatalogItem?>(null) }
    LaunchedEffect(Unit) { vm.event.collect(snack::showSnackbar) }; LaunchedEffect(order?.notes) { noteText = order?.notes.orEmpty() }
    Scaffold(topBar = { TopAppBar(title = { Text("Mesa") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }, snackbarHost = { SnackbarHost(snack) }, floatingActionButton = { FloatingActionButton(onClick = { if (order != null) showCatalog = true }) { Icon(Icons.Default.Add, "Adicionar") } }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) { Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(order?.notes?.takeIf { it.isNotBlank() } ?: "Sem observação da mesa"); TextButton(onClick = { if (editingNote) { vm.setNote(noteText); editingNote = false } else editingNote = true }) { Text(if (editingNote) "Salvar" else "Editar") } } }
            if (editingNote) OutlinedTextField(noteText, { noteText = it.take(280) }, Modifier.fillMaxWidth().padding(12.dp), placeholder = { Text("Ex.: aniversário, alergia…") }, minLines = 2)
            LazyColumn(Modifier.weight(1f).fillMaxWidth()) { items(items, key = { it.itemId }) { item -> ListItem(headlineContent = { Text("${item.quantity}x ${item.nameSnapshot}${item.weightG?.let { weight -> " (${weight}g)" }.orEmpty()}") }, supportingContent = { Text("R$ %.2f".format(item.priceSnapshot * item.quantity)) }, trailingContent = { Row { IconButton(onClick = { vm.send(item) }, enabled = !vm.isBusy(item.itemId)) { Icon(Icons.Default.Send, "Enviar") }; TextButton(onClick = { vm.cancel(item) }, enabled = !vm.isBusy(item.itemId)) { Text("X") } } }) } }
            order?.let { current -> Card(Modifier.fillMaxWidth().padding(12.dp)) { Column(Modifier.padding(12.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Subtotal"); Text("R$ %.2f".format(current.subtotal)) }; Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Taxa 10%"); Text("R$ %.2f".format(current.serviceFee)) }; HorizontalDivider(); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Total", style = MaterialTheme.typography.titleLarge); Text("R$ %.2f".format(current.total), style = MaterialTheme.typography.titleLarge) }; Button(onClick = { onCheckout(vm.tableId, current.orderId) }, Modifier.fillMaxWidth()) { Text("Fechar") } } } }
        }
    }
    if (showCatalog) ModalBottomSheet(onDismissRequest = { showCatalog = false }) { LazyColumn(Modifier.fillMaxWidth().padding(12.dp)) { items(catalog, key = { it.itemId }) { cat -> ListItem(headlineContent = { Text(cat.name) }, supportingContent = { Text(if (cat.pricingMode == "per_100g") "R$ %.2f / 100g".format(cat.price) else "R$ %.2f".format(cat.price)) }, modifier = Modifier.clickable { selected = cat; showCatalog = false }); HorizontalDivider() } } }
    selected?.let { cat -> AddItemDialog(cat, onDismiss = { selected = null }) { quantity, weight, note -> vm.addItem(cat, quantity, weight, note); selected = null } }
}

@Composable private fun AddItemDialog(item: CatalogItem, onDismiss: () -> Unit, onConfirm: (Int, Int?, String) -> Unit) {
    var quantity by remember { mutableStateOf("1") }; var weight by remember { mutableStateOf("") }; var note by remember { mutableStateOf("") }; val weighted = item.pricingMode == "per_100g"
    AlertDialog(onDismissRequest = onDismiss, title = { Text(item.name) }, text = { Column { if (weighted) Text("Preço: R$ %.2f por 100g".format(item.price)); OutlinedTextField(quantity, { quantity = it.filter(Char::isDigit) }, label = { Text("Quantidade") }); if (weighted) OutlinedTextField(weight, { weight = it.filter(Char::isDigit) }, label = { Text("Peso em gramas") }, supportingText = { Text((item.weightMaxG ?: Int.MAX_VALUE)?.let { "Máximo: ${it}g" } ?: "Informe o peso") }); OutlinedTextField(note, { note = it.take(280) }, label = { Text("Observação") }) } }, confirmButton = { TextButton(onClick = { val q = quantity.toIntOrNull() ?: 0; val g = weight.toIntOrNull(); if (q > 0 && (!weighted || (g != null && g > 0 && ((item.weightMaxG ?: Int.MAX_VALUE) == null || g <= (item.weightMaxG ?: Int.MAX_VALUE))))) onConfirm(q, g, note) }, enabled = quantity.toIntOrNull()?.let { it > 0 } == true && (!weighted || weight.toIntOrNull()?.let { it > 0 && ((item.weightMaxG ?: Int.MAX_VALUE) == null || it <= (item.weightMaxG ?: Int.MAX_VALUE)) } == true)) { Text("Adicionar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
