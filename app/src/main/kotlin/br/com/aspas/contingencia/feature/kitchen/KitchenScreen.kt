package br.com.aspas.contingencia.feature.kitchen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.PrintJobEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
@HiltViewModel
class KitchenViewModel @Inject constructor(db: AspasDatabase) : ViewModel() {
val jobs: StateFlow<List<PrintJobEntity>> = db.printJobDao().observeQueued()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
val failed: StateFlow<List<PrintJobEntity>> = db.printJobDao().observeFailed()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(onBack: () -> Unit, vm: KitchenViewModel = hiltViewModel()) {
val jobs by vm.jobs.collectAsState()
val failed by vm.failed.collectAsState()
val fmt = remember { SimpleDateFormat("HH:mm:ss", Locale("pt","BR")) }
Scaffold(topBar = { TopAppBar(title = { Text("Produção") },
navigationIcon = { IconButton(onClick = onBack) {
Icon(Icons.Default.ArrowBack, null) } }) }
) { padding ->
Column(Modifier.padding(padding).fillMaxSize()) {
if (failed.isNotEmpty()) {
Card(Modifier.fillMaxWidth().padding(8.dp),
colors = CardDefaults.cardColors(
containerColor = MaterialTheme.colorScheme.errorContainer)) {
Text("${failed.size} impressões falharam",
Modifier.padding(12.dp))
}
}
LazyColumn(Modifier.fillMaxSize()) {
items(jobs, key = { it.printId }) { j ->
ListItem(
headlineContent = { Text("⏳ ${j.printerId}") },
supportingContent = { Text("tentativas: ${j.attempts}") },
trailingContent = { Text(fmt.format(Date(j.createdAt))) })
HorizontalDivider()
}
}
}
}
}
