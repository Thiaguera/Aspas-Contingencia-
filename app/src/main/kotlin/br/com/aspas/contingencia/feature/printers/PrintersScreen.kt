package br.com.aspas.contingencia.feature.printers
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.Printer
import br.com.aspas.contingencia.domain.repository.PrinterRepository
import br.com.aspas.contingencia.print.driver.ElginI9Driver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PrintersViewModel @Inject constructor(
private val printerRepository: PrinterRepository
) : ViewModel() {
val printers: StateFlow<List<Printer>> = printerRepository.observeAll()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
fun pingAll() {
viewModelScope.launch {
printers.value.forEach { p ->
val ok = ElginI9Driver(p.ip, p.port).isOnline()
printerRepository.updateStatus(p.printerId,
if (ok) "online" else "offline", System.currentTimeMillis())
}
}
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintersScreen(onBack: () -> Unit, vm: PrintersViewModel = hiltViewModel()) {
val printers by vm.printers.collectAsState()
LaunchedEffect(Unit) { vm.pingAll() }
Scaffold(topBar = { TopAppBar(title = { Text("Impressoras") },
navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
actions = { IconButton(onClick = { vm.pingAll() }) { Icon(Icons.Default.Refresh, null) } }) }
) { padding ->
LazyColumn(Modifier.padding(padding).fillMaxSize()) {
items(printers, key = { it.printerId }) { p ->
ListItem(
headlineContent = { Text(p.name) },
supportingContent = { Text("${p.ip}:${p.port}") },
leadingContent = {
Box(Modifier.size(12.dp).background(when (p.lastStatus) {
"online" -> Color(0xFF52B788)
"offline" -> Color(0xFFE63946)
else -> Color.Gray })) })
HorizontalDivider()
}
}
}
}
