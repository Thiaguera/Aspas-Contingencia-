package br.com.aspas.contingencia.print
import br.com.aspas.contingencia.data.projection.ProjectionEngine
import br.com.aspas.contingencia.domain.service.PrintDispatcher
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class PrintScheduler @Inject constructor(
private val dispatcher: PrintDispatcher,
private val projectionEngine: ProjectionEngine
) {
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
private var job: Job? = null
fun start() {
if (job?.isActive == true) return
job = scope.launch {
while (isActive) {
runCatching {
if (!projectionEngine.isMaintenanceInProgress()) dispatcher.processQueue()
}.onFailure { Timber.e(it, "print q") }
delay(3_000)
}
}
}
fun stop() { job?.cancel() }
}
