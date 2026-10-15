package br.com.aspas.contingencia.data.repository
import kotlinx.serialization.decodeFromString
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.CatalogItemEntity
import br.com.aspas.contingencia.data.db.entity.OrderItemEntity
import br.com.aspas.contingencia.domain.model.*
import br.com.aspas.contingencia.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TableRepositoryImpl @Inject constructor(private val db: AspasDatabase) : TableRepository {
override fun observeAll() = db.tableDao().observeAll().map { l ->
l.map { Table(it.tableId, it.number, it.label, it.status, it.openedAt,
it.closedAt, it.currentLeaseId, it.serviceFeePercent) } }
override fun observeById(id: String) = db.tableDao().observeById(id).map {
it?.let { t -> Table(t.tableId, t.number, t.label, t.status, t.openedAt,
t.closedAt, t.currentLeaseId, t.serviceFeePercent) } }
override suspend fun getById(id: String) = db.tableDao().getById(id)?.let {
Table(it.tableId, it.number, it.label, it.status, it.openedAt,
it.closedAt, it.currentLeaseId, it.serviceFeePercent) }
}
@Singleton
class OrderRepositoryImpl @Inject constructor(
private val db: AspasDatabase, private val json: Json
) : OrderRepository {
override suspend fun activeByTable(tableId: String) = db.orderDao().activeByTable(tableId)?.let {
Order(it.orderId, it.tableId, it.openedBy, it.openedAt, it.closedAt,
it.status, it.subtotal, it.serviceFee, it.total, it.notes) }
override fun observeById(orderId: String) = db.orderDao().observeById(orderId).map {
it?.let { o -> Order(o.orderId, o.tableId, o.openedBy, o.openedAt, o.closedAt,
o.status, o.subtotal, o.serviceFee, o.total, o.notes) } }
override fun observeItems(orderId: String) = db.orderItemDao().observeByOrder(orderId).map { list ->
list.map { it.toDomain(json) } }
override fun observeAllOpen() = db.orderDao().observeAllOpen().map { l ->
l.map { Order(it.orderId, it.tableId, it.openedBy, it.openedAt, it.closedAt,
it.status, it.subtotal, it.serviceFee, it.total, it.notes) } }
}
fun OrderItemEntity.toDomain(json: Json): OrderItem {
val mods = modifiersJson?.let { runCatching { json.decodeFromString<Map<String, String>>(it) }.getOrNull() } ?: emptyMap()
return OrderItem(itemId, orderId, catalogItemId, nameSnapshot, priceSnapshot, priceOriginal,
quantity, weightG, mods, status, includedIn, includedKind, extraFor, addedBy, addedAt, emptyList())
}
@Singleton
class PrinterRepositoryImpl @Inject constructor(private val db: AspasDatabase) : PrinterRepository {
override fun observeAll() = db.printerDao().observeAll().map { l ->
l.map { Printer(it.printerId, it.name, it.ip, it.port, it.headerLabel,
it.groupId, it.enabled, it.lastSeenAt, it.lastStatus) } }
override suspend fun getById(id: String) = db.printerDao().getById(id)?.let {
Printer(it.printerId, it.name, it.ip, it.port, it.headerLabel,
it.groupId, it.enabled, it.lastSeenAt, it.lastStatus) }
override suspend fun updateStatus(id: String, status: String, at: Long) =
db.printerDao().updateStatus(id, status, at)
}
@Singleton
class CatalogRepositoryImpl @Inject constructor(
private val db: AspasDatabase, private val json: Json
) : CatalogRepository {
override fun observeCategories() = db.catalogDao().observeCategories().map { l ->
l.map { CatalogCategory(it.categoryId, it.name, it.printDestination, it.sortOrder) } }
override fun observeAvailableItems() = db.catalogDao().observeAvailableItems().map { l ->
l.map { it.toDomain(json) } }
override suspend fun getItem(id: String) = db.catalogDao().getItem(id)?.let { it.toDomain(json) }
}
fun CatalogItemEntity.toDomain(json: Json): CatalogItem {
val dests = runCatching { json.decodeFromString<List<String>>(printDestinationsJson) }.getOrDefault(emptyList())
return CatalogItem(itemId, name, categoryId, price, pricingMode, priceHalf, weightMaxG,
dests, available, description, origin, serves, tagsJson, parrillaBundleJson, weightTiersJson)
}
@Singleton
class UserRepositoryImpl @Inject constructor(private val db: AspasDatabase) : UserRepository {
override fun observeActive() = db.userDao().observeActive().map { l ->
l.map { User(it.userId, it.name, it.role, it.active) } }
override suspend fun getById(id: String) = db.userDao().getById(id)?.let {
User(it.userId, it.name, it.role, it.active) }
override suspend fun listActive() = db.userDao().listActive().map {
User(it.userId, it.name, it.role, it.active) }
}
@Singleton
class SyncStateRepositoryImpl @Inject constructor(private val db: AspasDatabase) : SyncStateRepository {
override fun observeAll() = db.syncStateDao().observeAll().map { l ->
l.map { SyncState(it.peerDeviceId, it.lastHlcSent, it.lastHlcReceived, it.lastSyncAt, it.status) } }
}
