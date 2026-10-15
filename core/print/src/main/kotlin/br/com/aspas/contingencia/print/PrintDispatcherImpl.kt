package br.com.aspas.contingencia.print
import kotlinx.serialization.encodeToString
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.PrintBatchEntity
import br.com.aspas.contingencia.data.db.entity.PrintJobEntity
import br.com.aspas.contingencia.data.repository.toDomain
import br.com.aspas.contingencia.data.projection.ProjectionEngine
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.model.*
import br.com.aspas.contingencia.domain.repository.EventRepository
import br.com.aspas.contingencia.domain.service.PrintDispatcher
import br.com.aspas.contingencia.print.driver.ElginI9Driver
import br.com.aspas.contingencia.print.render.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class PrintDispatcherImpl @Inject constructor(
private val db: AspasDatabase,
private val eventRepository: EventRepository,
private val json: Json,
private val itemRenderer: ItemTicketRenderer,
private val cancelRenderer: CancellationTicketRenderer,
private val testRenderer: TestTicketRenderer,
private val retryPolicy: RetryPolicy,
private val projectionEngine: ProjectionEngine,
private val maintenanceGate: br.com.aspas.contingencia.data.projection.MaintenanceGate
) : PrintDispatcher {
override suspend fun enqueueItem(item: OrderItem, orderId: String): PrintBatch =
withContext(Dispatchers.IO) {
val batchId = Uuid.new()
val jobs = item.printDestinations.map { PrintBatchQueuedPayload.PrintJobRef(Uuid.new(), it) }
val payload = PrintBatchQueuedPayload(batchId, orderId, item.itemId, "item",
item.addedBy, item.printDestinations, jobs)
eventRepository.append(EventType.PRINT_BATCH_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), item.addedBy)
PrintBatch(batchId, orderId, item.itemId, "item", jobs.size)
}
override suspend fun enqueueCancellation(item: OrderItem, orderId: String,
reason: String, cancelledBy: String): PrintBatch = withContext(Dispatchers.IO) {
val batchId = Uuid.new()
val jobs = item.printDestinations.map { PrintBatchQueuedPayload.PrintJobRef(Uuid.new(), it) }
val payload = PrintBatchQueuedPayload(batchId, orderId, item.itemId, "cancellation",
cancelledBy, item.printDestinations, jobs)
eventRepository.append(EventType.PRINT_BATCH_CANCELLATION_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), cancelledBy)
PrintBatch(batchId, orderId, item.itemId, "cancellation", jobs.size)
}
override suspend fun reprint(printId: String, requestedBy: String): PrintBatch =
withContext(Dispatchers.IO) {
val original = db.printJobDao().getById(printId) ?: error("job não existe")
val batchId = Uuid.new()
val newPrintId = Uuid.new()
val payload = PrintBatchQueuedPayload(batchId, original.batchId, original.batchId,
"reprint", requestedBy, listOf(original.printerId),
listOf(PrintBatchQueuedPayload.PrintJobRef(newPrintId, original.printerId)))
eventRepository.append(EventType.REPRINT_REQUESTED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), requestedBy)
PrintBatch(batchId, original.batchId, original.batchId, "reprint", 1)
}
override suspend fun enqueueTest(printerId: String, requestedBy: String): PrintBatch =
withContext(Dispatchers.IO) {
val batchId = Uuid.new()
val printId = Uuid.new()
val payload = PrintBatchQueuedPayload(batchId, "test", "test",
"test", requestedBy, listOf(printerId),
listOf(PrintBatchQueuedPayload.PrintJobRef(printId, printerId)))
eventRepository.append(EventType.PRINT_BATCH_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), requestedBy)
PrintBatch(batchId, "test", "test", "test", 1)
}
override suspend fun forwardToPeer(printId: String, peerDeviceId: String) {
eventRepository.append(EventType.PRINT_JOB_FORWARDED,
"""{"printId":"$printId","toDevice":"$peerDeviceId"}""", null)
}
override suspend fun processQueue(): Unit = withContext(Dispatchers.IO) {
    // Hold the shared gate until all sends finish; checking only a durable boolean
    // before fetching jobs leaves a rebuild/send race.
    maintenanceGate.withPhysicalProcessing {
        val due = db.printJobDao().dueJobs(System.currentTimeMillis(), 20)
        for (job in due) {
            runCatching { processOne(job) }.onFailure { Timber.e(it, "processOne ${job.printId}") }
        }
    }
    Unit
}
private suspend fun processOne(job: PrintJobEntity) {
val printer = db.printerDao().getById(job.printerId) ?: return
if (!printer.enabled) return
val batch = db.printJobDao().getBatch(job.batchId) ?: return
val bytes = renderBatch(batch, job, printer) ?: return
val driver = ElginI9Driver(printer.ip, printer.port)
val connected = driver.connect()
val sent = connected && driver.send(bytes)
driver.close()
val attempt = job.attempts + 1
if (sent) {
db.printJobDao().updateStatus(job.printId, "ok", attempt, null, null, System.currentTimeMillis())
db.printerDao().updateStatus(printer.printerId, "online", System.currentTimeMillis())
eventRepository.append(EventType.PRINT_JOB_COMPLETED,
json.encodeToString(PrintJobCompletedPayload.serializer(),
PrintJobCompletedPayload(job.printId, job.batchId, job.printerId,
System.currentTimeMillis())), null)
} else {
val next = retryPolicy.nextRetry(attempt)
val err = if (!connected) "connection refused" else "send failed"
db.printJobDao().updateStatus(job.printId,
if (attempt >= 5) "failed" else "queued", attempt, err, next, null)
db.printerDao().updateStatus(printer.printerId, "offline", System.currentTimeMillis())
eventRepository.append(EventType.PRINT_JOB_FAILED,
json.encodeToString(PrintJobFailedPayload.serializer(),
PrintJobFailedPayload(job.printId, job.batchId, job.printerId,
attempt, err, next)), null)
}
}
private suspend fun renderBatch(batch: PrintBatchEntity, job: PrintJobEntity,
printer: br.com.aspas.contingencia.data.db.entity.PrinterEntity): ByteArray? {
val printerDomain = Printer(printer.printerId, printer.name, printer.ip, printer.port,
printer.headerLabel, printer.groupId, printer.enabled, printer.lastSeenAt, printer.lastStatus)
if (batch.kind == "test") {
return testRenderer.render(TicketContext(printerDomain, 0, "—",
OrderItem.empty(), kind = TicketKind.TEST))
}
val entity = db.orderItemDao().getById(batch.orderItemId) ?: return null
val item = entity.toDomain(json)
val order = db.orderDao().getById(item.orderId) ?: return null
val table = db.tableDao().getById(order.tableId)
val user = db.userDao().getById(item.addedBy)
return when (batch.kind) {
"cancellation" -> cancelRenderer.render(TicketContext(
printer = printerDomain, tableNumber = table?.number ?: 0,
waiterName = user?.name ?: "—", orderItem = item,
orderNote = order.notes, kind = TicketKind.CANCELLATION,
cancelReason = batch.reason ?: "—", cancelledBy = batch.createdBy))
else -> {
val children = db.orderItemDao().childrenOf(item.itemId).map { it.toDomain(json) }
itemRenderer.render(TicketContext(
printer = printerDomain, tableNumber = table?.number ?: 0,
waiterName = user?.name ?: "—", orderItem = item,
orderNote = order.notes,
includedChildren = children.filter { it.includedIn == item.itemId },
extras = children.filter { it.extraFor == item.itemId },
kind = if (batch.kind == "reprint") TicketKind.REPRINT else TicketKind.ITEM,
isReprint = batch.kind == "reprint"))
}
}
}
}
