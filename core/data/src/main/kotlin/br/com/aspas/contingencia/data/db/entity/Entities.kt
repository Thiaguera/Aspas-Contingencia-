package br.com.aspas.contingencia.data.db.entity
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(tableName = "events", indices = [Index("hlc"), Index("type"), Index("synced"), Index("createdAt")])
data class EventEntity(@PrimaryKey val eventId: String, val hlc: String, val deviceId: String,
val userId: String?, val type: String, val payload: String, val createdAt: Long,
val synced: Boolean = false)
@Entity(tableName = "tables", indices = [Index(value = ["number"], unique = true), Index("status")])
data class TableEntity(@PrimaryKey val tableId: String, val number: Int, val label: String?,
val status: String, val openedAt: Long?, val closedAt: Long?, val currentLeaseId: String?,
val serviceFeePercent: Double = 10.0)
@Entity(tableName = "orders", indices = [Index("tableId"), Index("status"), Index("openedBy")])
data class OrderEntity(@PrimaryKey val orderId: String, val tableId: String, val openedBy: String,
val openedAt: Long, val closedAt: Long?, val status: String, val subtotal: Double,
val serviceFee: Double, val total: Double, val notes: String?)
@Entity(tableName = "order_items", indices = [Index("orderId"), Index("status"), Index("parentItemId")])
data class OrderItemEntity(@PrimaryKey val itemId: String, val orderId: String, val catalogItemId: String,
val nameSnapshot: String, val priceSnapshot: Double, val priceOriginal: Double?, val quantity: Int,
val weightG: Int?, val modifiersJson: String?, val status: String, val includedIn: String?,
val includedKind: String?, val extraFor: String?, val cancelledBy: String?, val cancelledAt: Long?,
val cancelReason: String?, val addedBy: String, val addedAt: Long,
val parentItemId: String? = null)
@Entity(tableName = "payments", indices = [Index("orderId"), Index("method")])
data class PaymentEntity(@PrimaryKey val paymentId: String, val orderId: String, val method: String,
val amount: Double, val splitGroup: String?, val registeredBy: String, val registeredAt: Long,
val reversed: Boolean = false)
@Entity(tableName = "leases", indices = [Index("tableId"), Index("status"), Index("expiresAt")])
data class LeaseEntity(@PrimaryKey val leaseId: String, val tableId: String, val userId: String,
val deviceId: String, val acquiredAt: Long, val renewedAt: Long, val expiresAt: Long,
val status: String, val releasedBy: String?, val releasedAt: Long?)
@Entity(tableName = "catalog_categories")
data class CatalogCategoryEntity(@PrimaryKey val categoryId: String, val name: String,
val printDestination: String, val sortOrder: Int)
@Entity(tableName = "catalog_items", indices = [Index("categoryId"), Index("available")])
data class CatalogItemEntity(@PrimaryKey val itemId: String, val name: String, val categoryId: String,
val price: Double, val pricingMode: String, val priceHalf: Double?, val weightMaxG: Int?,
val weightTiersJson: String?, val parrillaBundleJson: String?, val printDestinationsJson: String,
val modifiersJson: String?, val tagsJson: String?, val description: String?, val origin: String?,
val serves: Int?, val available: Boolean, val sortOrder: Int)
@Entity(tableName = "printers")
data class PrinterEntity(@PrimaryKey val printerId: String, val name: String, val ip: String,
val port: Int, val headerLabel: String, val groupId: String?, val enabled: Boolean,
val lastSeenAt: Long?, val lastStatus: String)
@Entity(tableName = "print_batches")
data class PrintBatchEntity(@PrimaryKey val batchId: String, val orderItemId: String,
val kind: String, val createdAt: Long, val createdBy: String, val reason: String?)
@Entity(tableName = "print_jobs", indices = [Index("batchId"), Index("printerId"), Index("status"), Index("nextRetryAt")])
data class PrintJobEntity(@PrimaryKey val printId: String, val batchId: String, val printerId: String,
val status: String, val attempts: Int, val lastError: String?, val nextRetryAt: Long?,
val forwardedTo: String?, val reprint: Boolean, val originalPrint: String?,
val createdAt: Long, val completedAt: Long?)
@Entity(tableName = "users")
data class UserEntity(@PrimaryKey val userId: String, val name: String, val role: String,
val pinHash: String, val pinSalt: String, val active: Boolean, val createdAt: Long,
val failedAttempts: Int = 0, val lockedUntil: Long? = null)
@Entity(tableName = "devices")
data class DeviceEntity(@PrimaryKey val deviceId: String, val name: String, val address: String,
val port: Int, val token: String, val trusted: Boolean, val pairedAt: Long?,
val pairedBy: String?, val lastSeenAt: Long?)
@Entity(tableName = "sync_state")
data class SyncStateEntity(@PrimaryKey val peerDeviceId: String, val lastHlcSent: String?,
val lastHlcReceived: String?, val lastSyncAt: Long?, val status: String)
@Entity(tableName = "audit_log", indices = [Index("createdAt"), Index("action")])
data class AuditLogEntity(@PrimaryKey val auditId: String, val eventId: String, val action: String,
val actorUserId: String, val actorDeviceId: String, val targetType: String?,
val targetId: String?, val details: String?, val createdAt: Long)
@Entity(tableName = "snapshots")
data class SnapshotEntity(@PrimaryKey val snapshotId: String, val lastHlc: String,
val createdAt: Long, val eventsUpTo: Int, val tablesJson: String, val ordersJson: String,
val itemsJson: String, val paymentsJson: String)
@Entity(tableName = "cash_sessions", indices = [Index("status"), Index("openedAt")])
data class CashSessionEntity(@PrimaryKey val sessionId: String, val openedBy: String,
val openedAt: Long, val openingAmount: Double, val closedBy: String?, val closedAt: Long?,
val closingAmount: Double?, val expectedAmount: Double?, val difference: Double?,
val status: String, val notes: String?)
@Entity(tableName = "cash_movements", indices = [Index("sessionId"), Index("kind")])
data class CashMovementEntity(@PrimaryKey val movementId: String, val sessionId: String,
val kind: String, val amount: Double, val reason: String, val registeredBy: String,
val registeredAt: Long)
