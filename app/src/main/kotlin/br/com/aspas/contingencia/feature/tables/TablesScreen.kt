package br.com.aspas.contingencia.feature.tables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.Table
import br.com.aspas.contingencia.domain.repository.TableRepository
import br.com.aspas.contingencia.domain.usecase.OpenTableUseCase
import br.com.aspas.contingencia.session.SessionManager
import br.com.aspas.contingencia.ui.theme.StatusFree
import br.com.aspas.contingencia.ui.theme.StatusOpen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TableUiEvent {
    data class Open(val tableId: String) : TableUiEvent
    data class Error(val message: String) : TableUiEvent
}

@HiltViewModel
class TablesViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val openTableUseCase: OpenTableUseCase,
    private val session: SessionManager
) : ViewModel() {
    val tables: StateFlow<List<Table>> = tableRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val opening = MutableStateFlow<Set<String>>(emptySet())
    private val _event = MutableSharedFlow<TableUiEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<TableUiEvent> = _event.asSharedFlow()

    val isManager: Boolean get() = session.currentRole == "manager"
    fun logout() { session.logout() }

    fun open(tableId: String) {
        if (tableId in opening.value) return
        val uid = session.currentUserId() ?: run {
            _event.tryEmit(TableUiEvent.Error("Sessão expirada"))
            return
        }
        opening.value = opening.value + tableId
        viewModelScope.launch {
            openTableUseCase(tableId, uid)
                .onSuccess { _event.emit(TableUiEvent.Open(tableId)) }
                .onFailure { _event.emit(TableUiEvent.Error(it.message ?: "Não foi possível abrir a mesa")) }
            opening.value = opening.value - tableId
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
    onOpen: (String) -> Unit,
    onPrinters: () -> Unit,
    onKitchen: () -> Unit,
    onManagement: () -> Unit,
    onLogout: () -> Unit,
    vm: TablesViewModel = hiltViewModel()
) {
    val tables by vm.tables.collectAsState()
    val openingIds by vm.opening.collectAsState()
    val snack = remember { SnackbarHostState() }
    var menuOpen by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event) {
                is TableUiEvent.Open -> onOpen(event.tableId)
                is TableUiEvent.Error -> snack.showSnackbar(event.message)
            }
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mesas") }, actions = {
            IconButton(onClick = onKitchen) { Icon(Icons.Default.Restaurant, "Produção") }
            IconButton(onClick = onPrinters) { Icon(Icons.Default.Print, "Impressoras") }
            IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "Menu") }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                if (vm.isManager) DropdownMenuItem(text = { Text("Gestão: equipe, caixa, relatórios e backup") }, onClick = { menuOpen = false; onManagement() })
                DropdownMenuItem(text = { Text("Trocar operador") }, onClick = { menuOpen = false; vm.logout(); onLogout() })
                DropdownMenuItem(text = { Text("Impressoras") }, onClick = { menuOpen = false; onPrinters() })
                DropdownMenuItem(text = { Text("Produção") }, onClick = { menuOpen = false; onKitchen() })
            }
        }) },
        snackbarHost = { SnackbarHost(snack) }
    ) { padding ->
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 96.dp),
            modifier = Modifier.padding(padding).fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tables, key = { it.tableId }) { table ->
                val opening = table.tableId in openingIds
                val bg = when (table.status) { "free" -> StatusFree; "open" -> StatusOpen; else -> Color.LightGray }
                Card(onClick = { vm.open(table.tableId) }, enabled = !opening,
                    modifier = Modifier.aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = bg)) {
                    Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Text("${table.number}", style = MaterialTheme.typography.headlineMedium)
                        if (opening) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text(table.status, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
