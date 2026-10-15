package br.com.aspas.contingencia.domain.usecase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.model.CatalogItem
import br.com.aspas.contingencia.domain.model.Order
import br.com.aspas.contingencia.domain.model.OrderItem
import br.com.aspas.contingencia.domain.repository.*
import java.math.BigDecimal
import java.math.RoundingMode
import br.com.aspas.contingencia.domain.service.LeaseManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
class OpenTableUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val leaseManager: LeaseManager,
private val tableRepository: TableRepository,
private val orderRepository: OrderRepository,
private val json: Json
) {
private val mutex = Mutex()
suspend operator fun invoke(tableId: String, userId: String): Result<String> = mutex.withLock {
val table = tableRepository.getById(tableId)
?: return@withLock Result.failure(IllegalStateException("mesa inexistente"))

// A table projection can lag the event log. Reopening an already open table is idempotent.
if (table.status != "free") {
val active = orderRepository.activeByTable(tableId)
return@withLock if (active != null) Result.success(active.orderId)
else Result.failure(IllegalStateException("mesa ocupada"))
}

// Acquire the lease before appending TABLE_OPENED. This avoids creating an order when another
// operator already owns the table; projection will subsequently expose the opened order.
when (leaseManager.acquire(tableId, userId)) {
is LeaseManager.AcquireResult.Success -> Unit
is LeaseManager.AcquireResult.Busy -> return@withLock Result.failure(IllegalStateException("mesa em uso"))
is LeaseManager.AcquireResult.Failed -> return@withLock Result.failure(IllegalStateException("falha no lease"))
}
val orderId = Uuid.new()
return@withLock try {
eventRepository.append(EventType.TABLE_OPENED,
json.encodeToString(TableOpenedPayload.serializer(), TableOpenedPayload(tableId, orderId, userId)), userId = userId)
Result.success(orderId)
} catch (error: Throwable) {
leaseManager.release(tableId, userId, "open_failed")
Result.failure(error)
}
}
}
class AddOrderItemUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val json: Json
) {
suspend operator fun invoke(
orderId: String, catalogItemId: String, nameSnapshot: String,
price: Double, quantity: Int = 1,
modifiers: Map<String, String> = emptyMap(),
includedIn: String? = null, includedKind: String? = null,
extraFor: String? = null, priceOriginal: Double? = null,
weightG: Int? = null, userId: String
): Result<String> = runCatching {
val itemId = Uuid.new()
val payload = OrderItemAddedPayload(orderId, itemId, catalogItemId,
nameSnapshot, price, priceOriginal, quantity, weightG, null,
modifiers, includedIn, includedKind, extraFor, userId)
eventRepository.append(EventType.ORDER_ITEM_ADDED,
json.encodeToString(OrderItemAddedPayload.serializer(), payload), userId = userId)
itemId
}
}
class CancelOrderItemUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val catalogRepository: CatalogRepository,
private val json: Json
) {
suspend operator fun invoke(item: OrderItem, reason: String, userId: String): Result<Unit> =
runCatching {
val cat = catalogRepository.getItem(item.catalogItemId)
val payload = OrderItemCancelledPayload(item.orderId, item.itemId,
userId, reason, cat?.printDestinations ?: emptyList())
eventRepository.append(EventType.ORDER_ITEM_CANCELLED,
json.encodeToString(OrderItemCancelledPayload.serializer(), payload), userId = userId)
}
}
class SendToProductionUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val catalogRepository: CatalogRepository,
private val json: Json
) {
suspend operator fun invoke(item: OrderItem): Result<Int> = runCatching {
val cat = catalogRepository.getItem(item.catalogItemId) ?: error("item não existe")
val destinations = cat.printDestinations
val batchId = Uuid.new()
val jobs = destinations.map { PrintBatchQueuedPayload.PrintJobRef(Uuid.new(), it) }
val payload = PrintBatchQueuedPayload(batchId, item.orderId, item.itemId, "item",
item.addedBy, destinations, jobs)
eventRepository.append(EventType.PRINT_BATCH_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), userId = item.addedBy)
jobs.size
}
}
class CloseTableUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val leaseManager: LeaseManager,
private val json: Json
) {
private val mutex = Mutex()
suspend operator fun invoke(
tableId: String, order: Order,
payments: List<Triple<String, Double, String?>>, userId: String
): Result<Unit> = mutex.withLock {
runCatching {
require(order.status == "open") { "comanda não está aberta" }
require(payments.isNotEmpty()) { "sem pagamentos" }
require(payments.all { it.first.isNotBlank() && it.second > 0.0 && it.second.isFinite() }) { "pagamento inválido" }
val fee = money(order.serviceFee)
val total = money(order.total)
val paid = payments.fold(BigDecimal.ZERO) { sum, payment -> sum + money(payment.second) }
require(paid == total) { "pagamentos não conferem" }

// Persist payments and the close marker in one SQLite transaction. A retry checks the
// durable marker in the event log, so it is safe even while projections are catching up.
val requests = buildList {
    payments.forEach { (method, amount, split) ->
        add(EventAppendRequest(EventType.PAYMENT_REGISTERED,
            json.encodeToString(PaymentRegisteredPayload.serializer(),
                PaymentRegisteredPayload(order.orderId, Uuid.new(), method, money(amount).toDouble(), split, userId)), userId))
    }
    add(EventAppendRequest(EventType.TABLE_CLOSED,
        json.encodeToString(TableClosedPayload.serializer(),
            TableClosedPayload(tableId, order.orderId, money(order.subtotal).toDouble(), fee.toDouble(), total.toDouble(), userId)), userId))
}
val result = eventRepository.appendCloseBatch(tableId, order.orderId, requests)
if (result.appended) leaseManager.release(tableId, userId, "closed")
}
}
}
private fun money(value: Double): BigDecimal =
    BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP)

class AddOrderNoteUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val json: Json
) {
suspend operator fun invoke(orderId: String, note: String, userId: String): Result<Unit> =
runCatching {
eventRepository.append(EventType.ORDER_NOTE_ADDED,
json.encodeToString(OrderNoteAddedPayload.serializer(),
OrderNoteAddedPayload(orderId, note.trim().take(280), userId)), userId = userId)
}
}
class ParrillaBundleUseCase(private val json: Json) {
@Serializable
data class FixedBundle(val included_guarnicoes: Int = 0, val included_acompanhamentos: Int = 0)
@Serializable
data class WeightTier(val max_weight_g: Int, val free_items: Int)
sealed interface BundleInfo {
data class Fixed(val guarnicoes: Int, val acompanhamentos: Int) : BundleInfo
data class WeightBased(val maxWeightG: Int, val tiers: List<WeightTier>) : BundleInfo {
fun freeItemsFor(weightG: Int): Int =
tiers.firstOrNull { weightG <= it.max_weight_g }?.free_items ?: tiers.last().free_items
}
}
fun resolve(item: CatalogItem): BundleInfo? {
item.parrillaBundleJson?.let { raw ->
val fixed = runCatching { json.decodeFromString<FixedBundle>(raw) }.getOrNull()
if (fixed != null) return BundleInfo.Fixed(fixed.included_guarnicoes, fixed.included_acompanhamentos)
}
item.weightTiersJson?.let { raw ->
val tiers = runCatching { json.decodeFromString<List<WeightTier>>(raw) }.getOrNull()
if (tiers != null && item.weightMaxG != null) return BundleInfo.WeightBased(item.weightMaxG, tiers)
}
return null
}
}
