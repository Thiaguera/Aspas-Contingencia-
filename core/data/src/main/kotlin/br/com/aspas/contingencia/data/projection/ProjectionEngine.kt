package br.com.aspas.contingencia.data.projection
import androidx.room.withTransaction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.*
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.repository.EventRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ProjectionEngine @Inject constructor(
private val db: AspasDatabase,
private val json: Json,
private val eventRepository: EventRepository,
private val maintenanceGate: MaintenanceGate
) {
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
private var started = false
private val applyMutex = Mutex()
fun start() {
if (started) return
started = true
scope.launch {
eventRepository.observeAll().collect { e ->
runCatching { apply(e) }.onFailure { Timber.e(it, "projection ${e.type}") }
}
}
}
/** Applies once. Projection writes and durable marker commit atomically. */
suspend fun apply(e: EventEnvelope) = applyMutex.withLock {
    db.withTransaction {
        if (db.appliedEventDao().isApplied(e.eventId)) return@withTransaction
        applyInternal(e, replaying = false)
        db.appliedEventDao().insert(AppliedEventEntity(e.eventId, e.hlc, System.currentTimeMillis(), "live"))
    }
}

/**
 * Deliberate maintenance operation; it is never called at startup. Print queue
 * events are intentionally ignored, therefore a rebuild cannot produce paper.
 */
suspend fun rebuildFromHistory(): Int = maintenanceGate.withExclusiveMaintenance {
    applyMutex.withLock {
    // This durable flag is written before destructive work. A process crash leaves it
    // set and physical printing fail-closed until an operator explicitly rebuilds again.
    // Rebuild never reconciles v1/legacy state by itself. Preserve its checkpoint so
    // queued physical work remains quarantined until a separate explicit reconciliation.
    val legacyCheckpointHlc = db.projectionStateDao().get()?.legacyCheckpointHlc
    db.projectionStateDao().put(ProjectionStateEntity(
        rebuildInProgress = true,
        legacyCheckpointHlc = legacyCheckpointHlc,
        updatedAt = System.currentTimeMillis()
    ))
    var applied = 0
    db.withTransaction {
        clearEventProjectionsForRebuild()
        db.appliedEventDao().clear()
        val events = db.eventDao().all()
        events.forEach { entity ->
            val envelope = EventEnvelope(entity.eventId, entity.hlc, entity.deviceId, entity.userId, entity.type, entity.payload, entity.createdAt)
            // All physical print events are skipped during rebuild. Retained print
            // tables are authoritative and must not be changed by historical events.
            applyInternal(envelope, replaying = true)
            db.appliedEventDao().insert(AppliedEventEntity(envelope.eventId, envelope.hlc, System.currentTimeMillis(), "rebuild"))
            applied++
        }
    }
    // Clear maintenance only after the complete replay transaction commits. On any
    // failure the durable true flag remains, so printing fails closed after restart.
    db.projectionStateDao().put(ProjectionStateEntity(
        rebuildInProgress = false,
        legacyCheckpointHlc = legacyCheckpointHlc,
        updatedAt = System.currentTimeMillis()
    ))
    applied
    }
}

suspend fun isMaintenanceInProgress(): Boolean =
    db.projectionStateDao().get()?.let { it.rebuildInProgress || it.legacyCheckpointHlc != null } == true

private suspend fun clearEventProjectionsForRebuild() {
    // tables is provisioned configuration. Preserve its rows and reset only fields
    // owned by the event projection; catalog/users/printers and all physical print
    // tables are likewise retained.
    db.openHelper.writableDatabase.execSQL("UPDATE `tables` SET status = 'free', openedAt = NULL, closedAt = NULL, currentLeaseId = NULL")
    listOf("cash_movements", "cash_sessions", "audit_log", "payments", "order_items", "orders", "leases")
        .forEach { db.openHelper.writableDatabase.execSQL("DELETE FROM `$it`") }
}

private suspend fun applyInternal(e: EventEnvelope, replaying: Boolean) {
when (e.type) {
EventType.TABLE_OPENED -> onTableOpened(e)
EventType.TABLE_CLOSED -> onTableClosed(e)
EventType.TABLE_LEASE_ACQUIRED -> onLeaseAcquired(e)
EventType.TABLE_LEASE_RENEWED -> onLeaseRenewed(e)
EventType.TABLE_LEASE_RELEASED -> onLeaseReleased(e)
EventType.TABLE_LEASE_EXPIRED -> onLeaseExpired(e)
EventType.TABLE_LEASE_FORCED -> onLeaseForced(e)
EventType.ORDER_ITEM_ADDED -> onOrderItemAdded(e)
EventType.ORDER_ITEM_CANCELLED -> onOrderItemCancelled(e)
EventType.ORDER_NOTE_ADDED -> onOrderNoteAdded(e)
EventType.ORDER_ITEM_PRICE_ADJUSTED -> onPriceAdjusted(e)
EventType.ORDER_ITEM_COMPED -> onComped(e)
EventType.ORDER_VOIDED -> onVoided(e)
EventType.PAYMENT_REGISTERED -> onPaymentRegistered(e)
EventType.PAYMENT_REVERSED -> onPaymentReversed(e)
EventType.SERVICE_FEE_REMOVED -> onFeeRemoved(e)
// Every physical print event is ignored during a rebuild. Retained print
// tables and printer state are authoritative; replay must never send or alter them.
EventType.PRINT_BATCH_QUEUED, EventType.PRINT_BATCH_CANCELLATION_QUEUED,
EventType.REPRINT_REQUESTED -> if (!replaying) onPrintBatchQueued(e)
EventType.PRINT_JOB_COMPLETED -> if (!replaying) onPrintJobCompleted(e)
EventType.PRINT_JOB_FAILED -> if (!replaying) onPrintJobFailed(e)
EventType.PRINT_JOB_FORWARDED -> Unit
EventType.PRINTER_STATUS_CHANGED -> if (!replaying) onPrinterStatus(e)
EventType.AUDIT_ACTION_RECORDED -> onAudit(e)
EventType.CATALOG_ITEM_AVAILABILITY_CHANGED -> onCatalogAvailability(e)
EventType.CASH_SESSION_OPENED -> onCashOpened(e)
EventType.CASH_SESSION_CLOSED -> onCashClosed(e)
EventType.CASH_MOVEMENT -> onCashMovement(e)
}
}
private suspend fun onTableOpened(e: EventEnvelope) {
val p = json.decodeFromString<TableOpenedPayload>(e.payload)
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(status = "open", openedAt = e.createdAt, closedAt = null)) }
db.orderDao().upsert(OrderEntity(p.orderId, p.tableId, p.openedBy, e.createdAt, null,
"open", 0.0, 0.0, 0.0, null))
}
private suspend fun onTableClosed(e: EventEnvelope) {
val p = json.decodeFromString<TableClosedPayload>(e.payload)
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(status = "free", closedAt = e.createdAt, currentLeaseId = null)) }
db.orderDao().getById(p.orderId)?.let {
db.orderDao().upsert(it.copy(status = "closed", closedAt = e.createdAt,
subtotal = p.subtotal, serviceFee = p.serviceFee, total = p.total)) }
}
private suspend fun onLeaseAcquired(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseAcquiredPayload>(e.payload)
db.leaseDao().upsert(LeaseEntity(p.leaseId, p.tableId, p.userId, p.deviceId,
e.createdAt, e.createdAt, p.expiresAt, "active", null, null))
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(currentLeaseId = p.leaseId)) }
}
private suspend fun onLeaseRenewed(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseRenewedPayload>(e.payload)
db.leaseDao().getById(p.leaseId)?.let {
db.leaseDao().upsert(it.copy(renewedAt = p.renewedAt, expiresAt = p.expiresAt)) }
}
private suspend fun onLeaseReleased(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseReleasedPayload>(e.payload)
db.leaseDao().markReleased(p.leaseId, "released", e.createdAt, p.releasedBy)
}
private suspend fun onLeaseExpired(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseExpiredPayload>(e.payload)
db.leaseDao().markReleased(p.leaseId, "expired", p.detectedAt, null)
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(currentLeaseId = null)) }
}
private suspend fun onLeaseForced(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseForcedPayload>(e.payload)
db.leaseDao().markReleased(p.leaseId, "forced", e.createdAt, p.forcedBy)
}
private suspend fun onOrderItemAdded(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemAddedPayload>(e.payload)
db.orderItemDao().upsert(OrderItemEntity(p.itemId, p.orderId, p.catalogItemId,
p.nameSnapshot, p.priceSnapshot, p.priceOriginal, p.quantity, p.weightG,
if (p.modifiers.isEmpty()) null else json.encodeToString(p.modifiers),
"active", p.includedIn, p.includedKind, p.extraFor, null, null, null,
p.addedBy, e.createdAt, p.includedIn ?: p.extraFor))
recomputeTotals(p.orderId)
}
private suspend fun onOrderItemCancelled(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemCancelledPayload>(e.payload)
db.orderItemDao().getById(p.itemId)?.let {
db.orderItemDao().upsert(it.copy(status = "cancelled", cancelledBy = p.cancelledBy,
cancelledAt = e.createdAt, cancelReason = p.reason)) }
recomputeTotals(p.orderId)
}
private suspend fun onOrderNoteAdded(e: EventEnvelope) {
val p = json.decodeFromString<OrderNoteAddedPayload>(e.payload)
db.orderDao().getById(p.orderId)?.let { db.orderDao().upsert(it.copy(notes = p.note)) }
}
private suspend fun onPriceAdjusted(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemPriceAdjustedPayload>(e.payload)
db.orderItemDao().getById(p.itemId)?.let { db.orderItemDao().upsert(it.copy(priceSnapshot = p.newPrice)) }
recomputeTotals(p.orderId)
}
private suspend fun onComped(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemCompedPayload>(e.payload)
db.orderItemDao().getById(p.itemId)?.let { db.orderItemDao().upsert(it.copy(priceSnapshot = 0.0)) }
recomputeTotals(p.orderId)
}
private suspend fun onVoided(e: EventEnvelope) {
val p = json.decodeFromString<OrderVoidedPayload>(e.payload)
db.orderDao().getById(p.orderId)?.let {
db.orderDao().upsert(it.copy(status = "voided", subtotal = 0.0, serviceFee = 0.0,
total = 0.0, closedAt = e.createdAt)) }
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(status = "free", closedAt = e.createdAt)) }
}
private suspend fun onPaymentRegistered(e: EventEnvelope) {
val p = json.decodeFromString<PaymentRegisteredPayload>(e.payload)
db.paymentDao().upsert(PaymentEntity(p.paymentId, p.orderId, p.method,
p.amount, p.splitGroup, p.registeredBy, e.createdAt, false))
}
private suspend fun onPaymentReversed(e: EventEnvelope) {
val p = json.decodeFromString<PaymentReversedPayload>(e.payload)
db.paymentDao().getById(p.paymentId)?.let { db.paymentDao().upsert(it.copy(reversed = true)) }
}
private suspend fun onFeeRemoved(e: EventEnvelope) {
val p = json.decodeFromString<ServiceFeeRemovedPayload>(e.payload)
db.orderDao().getById(p.orderId)?.let {
db.orderDao().upsert(it.copy(serviceFee = 0.0, total = it.subtotal)) }
}
private suspend fun onPrintBatchQueued(e: EventEnvelope) {
val p = json.decodeFromString<PrintBatchQueuedPayload>(e.payload)
val kind = when (e.type) {
EventType.PRINT_BATCH_CANCELLATION_QUEUED -> "cancellation"
EventType.REPRINT_REQUESTED -> "reprint"
else -> p.kind }
db.printJobDao().upsertBatch(PrintBatchEntity(p.batchId, p.itemId, kind,
e.createdAt, p.createdBy, null))
p.jobs.forEach { ref ->
db.printJobDao().upsertJob(PrintJobEntity(ref.printId, p.batchId, ref.printerId,
"queued", 0, null, null, null, kind == "reprint", null, e.createdAt, null)) }
}
private suspend fun onPrintJobCompleted(e: EventEnvelope) {
val p = json.decodeFromString<PrintJobCompletedPayload>(e.payload)
db.printJobDao().updateStatus(p.printId, "ok", 0, null, null, p.completedAt)
}
private suspend fun onPrintJobFailed(e: EventEnvelope) {
val p = json.decodeFromString<PrintJobFailedPayload>(e.payload)
db.printJobDao().updateStatus(p.printId,
if (p.attempt >= 5) "failed" else "queued", p.attempt, p.error, p.nextRetryAt, null)
}
private suspend fun onPrinterStatus(e: EventEnvelope) {
val p = json.decodeFromString<PrinterStatusChangedPayload>(e.payload)
db.printerDao().updateStatus(p.printerId, p.newStatus, p.detectedAt)
}
private suspend fun onAudit(e: EventEnvelope) {
val p = json.decodeFromString<AuditActionRecordedPayload>(e.payload)
db.auditDao().insert(AuditLogEntity(e.eventId, e.eventId, p.action, p.actorUserId,
p.actorDeviceId, p.targetType, p.targetId, json.encodeToString(p.details), e.createdAt))
}
private suspend fun onCatalogAvailability(e: EventEnvelope) {
val p = json.decodeFromString<CatalogItemAvailabilityChangedPayload>(e.payload)
db.catalogDao().getItem(p.itemId)?.let {
db.catalogDao().upsertItem(it.copy(available = p.available)) }
}
private suspend fun onCashOpened(e: EventEnvelope) {
val p = json.decodeFromString<CashSessionOpenedPayload>(e.payload)
db.cashSessionDao().upsert(CashSessionEntity(p.sessionId, p.openedBy, p.openedAt,
p.openingAmount, null, null, null, null, null, "open", null))
}
private suspend fun onCashClosed(e: EventEnvelope) {
val p = json.decodeFromString<CashSessionClosedPayload>(e.payload)
db.cashSessionDao().getById(p.sessionId)?.let {
db.cashSessionDao().upsert(it.copy(closedBy = p.closedBy, closedAt = e.createdAt,
closingAmount = p.closingAmount, expectedAmount = p.expectedAmount,
difference = p.difference, status = "closed", notes = p.notes)) }
}
private suspend fun onCashMovement(e: EventEnvelope) {
val p = json.decodeFromString<CashMovementPayload>(e.payload)
db.cashMovementDao().insert(CashMovementEntity(p.movementId, p.sessionId, p.kind,
p.amount, p.reason, p.registeredBy, e.createdAt))
}
private suspend fun recomputeTotals(orderId: String) {
val items = db.orderItemDao().listByOrder(orderId).filter { it.status == "active" }
val subtotal = items.sumOf { it.priceSnapshot * it.quantity }
val order = db.orderDao().getById(orderId) ?: return
val fee = subtotal * 0.10
db.orderDao().updateTotals(orderId, subtotal, fee, subtotal + fee)
}
}
