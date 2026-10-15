package br.com.aspas.contingencia.domain.model
data class Table(val tableId: String, val number: Int, val label: String?,
val status: String, val openedAt: Long?, val closedAt: Long?,
val currentLeaseId: String?, val serviceFeePercent: Double) {
val isFree get() = status == "free"
val isOpen get() = status == "open"
}
data class Order(val orderId: String, val tableId: String, val openedBy: String,
val openedAt: Long, val closedAt: Long?, val status: String,
val subtotal: Double, val serviceFee: Double, val total: Double, val notes: String?)
data class OrderItem(val itemId: String, val orderId: String, val catalogItemId: String,
val nameSnapshot: String, val priceSnapshot: Double, val priceOriginal: Double?,
val quantity: Int, val weightG: Int?, val modifiers: Map<String, String>,
val status: String, val includedIn: String?, val includedKind: String?,
val extraFor: String?, val addedBy: String, val addedAt: Long,
val printDestinations: List<String> = emptyList()) {
val isActive get() = status == "active"
companion object {
fun empty() = OrderItem("","","","—",0.0,null,1,null, emptyMap(),
"active",null,null,null,"",0L, emptyList())
}
}
data class CatalogCategory(val categoryId: String, val name: String,
val printDestination: String, val sortOrder: Int)
data class CatalogItem(val itemId: String, val name: String, val categoryId: String,
val price: Double, val pricingMode: String, val priceHalf: Double?,
val weightMaxG: Int?, val printDestinations: List<String>,
val available: Boolean, val description: String?, val origin: String?,
val serves: Int?, val tagsJson: String?, val parrillaBundleJson: String?,
val weightTiersJson: String?)
data class Printer(val printerId: String, val name: String, val ip: String, val port: Int,
val headerLabel: String, val groupId: String?, val enabled: Boolean,
val lastSeenAt: Long?, val lastStatus: String)
data class PrintBatch(val batchId: String, val orderId: String, val itemId: String,
val kind: String, val jobCount: Int)
data class User(val userId: String, val name: String, val role: String, val active: Boolean)
data class Lease(val leaseId: String, val tableId: String, val userId: String,
val deviceId: String, val acquiredAt: Long, val renewedAt: Long,
val expiresAt: Long, val status: String)
data class SyncState(val peerDeviceId: String, val lastHlcSent: String?,
val lastHlcReceived: String?, val lastSyncAt: Long?, val status: String)
