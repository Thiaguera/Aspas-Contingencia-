package br.com.aspas.contingencia.feature.tables
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Print
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
@HiltViewModel
class TablesViewModel @Inject constructor(
private val tableRepository: TableRepository,
private val openTableUseCase: OpenTableUseCase,
private val session: SessionManager
) : ViewModel() {
val tables: StateFlow<List<Table>> = tableRepository.observeAll()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
private val _event = MutableSharedFlow<String>()
val event: SharedFlow<String> = _event.asSharedFlow()
fun open(tableId: String) {
val uid = session.currentUserId() ?: return
viewModelScope.launch {
openTableUseCase(tableId, uid)
.onSuccess { _event.emit(tableId) }
.onFailure { _event.emit("erro: ${it.message}") }
}
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
onOpen: (String) -> Unit,
onPrinters: () -> Unit,
onKitchen: () -> Unit,
vm: TablesViewModel = hiltViewModel()
) {
val tables by vm.tables.collectAsState()
val snack = remember { SnackbarHostState() }
var menuOpen by remember { mutableStateOf(false) }
LaunchedEffect(Unit) {
vm.event.collect { onOpen(it) }
}
Scaffold(
topBar = {
TopAppBar(
title = { Text("Mesas") },
actions = {
IconButton(onClick = onKitchen) { Icon(Icons.Default.Restaurant, "Produção") }
IconButton(onClick = onPrinters) { Icon(Icons.Default.Print, "Impressoras") }
IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "Menu") }
DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
DropdownMenuItem(text = { Text("Impressoras") },
onClick = { menuOpen = false; onPrinters() })
DropdownMenuItem(text = { Text("Produção") },
onClick = { menuOpen = false; onKitchen() })
}
}
)
},
snackbarHost = { SnackbarHost(snack) }
) { padding ->
LazyVerticalGrid(
columns = GridCells.Adaptive(minSize = 96.dp),
modifier = Modifier.padding(padding).fillMaxSize().padding(12.dp),
horizontalArrangement = Arrangement.spacedBy(8.dp),
verticalArrangement = Arrangement.spacedBy(8.dp)
) {
items(tables, key = { it.tableId }) { t ->
val bg = when (t.status) {
"free" -> StatusFree
"open" -> StatusOpen
else -> Color.LightGray }
Card(onClick = { vm.open(t.tableId) },
modifier = Modifier.aspectRatio(1f),
colors = CardDefaults.cardColors(containerColor = bg)) {
Column(Modifier.fillMaxSize().padding(8.dp),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.Center) {
Text("${t.number}", style = MaterialTheme.typography.headlineMedium)
Text(t.status, style = MaterialTheme.typography.labelSmall)
}
}
}
}
}
}
