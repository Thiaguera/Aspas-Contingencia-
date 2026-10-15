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
import br.com.aspas.contingencia.domain.service.LeaseManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
class OpenTableUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val leaseManager: LeaseManager,
private val tableRepository: TableRepository,
private val json: Json
) {
suspend operator fun invoke(tableId: String, userId: String): Result<String> {
val table = tableRepository.getById(tableId)
?: return Result.failure(IllegalStateException("mesa inexistente"))
if (table.status != "free") return Result.failure(IllegalStateException("mesa ocupada"))
val orderId = Uuid.new()
eventRepository.append(EventType.TABLE_OPENED,
json.encodeToString(TableOpenedPayload.serializer(),
TableOpenedPayload(tableId, orderId, userId)), userId = userId)
return when (val r = leaseManager.acquire(tableId, userId)) {
is LeaseManager.AcquireResult.Success -> Result.success(orderId)
else -> Result.failure(IllegalStateException("falha no lease"))
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
suspend operator fun invoke(
tableId: String, order: Order,
payments: List<Triple<String, Double, String?>>, userId: String
): Result<Unit> = runCatching {
require(payments.isNotEmpty()) { "sem pagamentos" }
val fee = order.subtotal * 0.10
val total = order.subtotal + fee
val paid = payments.sumOf { it.second }
require(kotlin.math.abs(paid - total) < 0.01) { "pagamentos não conferem" }
payments.forEach { (method, amount, split) ->
eventRepository.append(EventType.PAYMENT_REGISTERED,
json.encodeToString(PaymentRegisteredPayload.serializer(),
PaymentRegisteredPayload(order.orderId, Uuid.new(), method, amount, split, userId)),
userId = userId)
}
eventRepository.append(EventType.TABLE_CLOSED,
json.encodeToString(TableClosedPayload.serializer(),
TableClosedPayload(tableId, order.orderId, order.subtotal, fee, total, userId)),
userId = userId)
leaseManager.release(tableId, userId, "closed")
}
}
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
