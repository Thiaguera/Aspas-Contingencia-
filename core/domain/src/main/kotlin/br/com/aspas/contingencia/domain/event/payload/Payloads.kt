package br.com.aspas.contingencia.domain.event.payload
import kotlinx.serialization.Serializable
@Serializable data class TableOpenedPayload(val tableId: String, val orderId: String, val openedBy: String)
@Serializable data class TableClosedPayload(val tableId: String, val orderId: String,
val subtotal: Double, val serviceFee: Double, val total: Double, val closedBy: String)
@Serializable data class TableLeaseAcquiredPayload(val leaseId: String, val tableId: String,
val userId: String, val deviceId: String, val expiresAt: Long)
@Serializable data class TableLeaseRenewedPayload(val leaseId: String, val renewedAt: Long, val expiresAt: Long)
@Serializable data class TableLeaseReleasedPayload(val leaseId: String, val releasedBy: String, val reason: String)
@Serializable data class TableLeaseExpiredPayload(val leaseId: String, val tableId: String, val detectedAt: Long)
@Serializable data class TableLeaseForcedPayload(val leaseId: String, val tableId: String,
val forcedBy: String, val reason: String)
@Serializable data class OrderItemAddedPayload(
val orderId: String, val itemId: String, val catalogItemId: String,
val nameSnapshot: String, val priceSnapshot: Double, val priceOriginal: Double? = null,
val quantity: Int, val weightG: Int? = null, val freeItemsAllowed: Int? = null,
val modifiers: Map<String, String> = emptyMap(),
val includedIn: String? = null, val includedKind: String? = null,
val extraFor: String? = null, val addedBy: String)
@Serializable data class OrderItemCancelledPayload(val orderId: String, val itemId: String,
val cancelledBy: String, val reason: String, val printDestinations: List<String> = emptyList())
@Serializable data class OrderItemPriceAdjustedPayload(val orderId: String, val itemId: String,
val newPrice: Double, val reason: String, val adjustedBy: String)
@Serializable data class OrderItemCompedPayload(val orderId: String, val itemId: String,
val reason: String, val compedBy: String)
@Serializable data class OrderVoidedPayload(val orderId: String, val tableId: String,
val reason: String, val voidedBy: String)
@Serializable data class OrderNoteAddedPayload(val orderId: String, val note: String, val addedBy: String)
@Serializable data class PaymentRegisteredPayload(val orderId: String, val paymentId: String,
val method: String, val amount: Double, val splitGroup: String? = null, val registeredBy: String)
@Serializable data class PaymentReversedPayload(val orderId: String, val paymentId: String,
val reversedBy: String, val reason: String)
@Serializable data class ServiceFeeRemovedPayload(val orderId: String, val previousPercent: Double,
val removedBy: String, val reason: String)
@Serializable data class PrintBatchQueuedPayload(
val batchId: String, val orderId: String, val itemId: String,
val kind: String, val createdBy: String,
val destinations: List<String> = emptyList(),
val jobs: List<PrintJobRef> = emptyList()) {
@Serializable data class PrintJobRef(val printId: String, val printerId: String)
}
@Serializable data class PrintJobCompletedPayload(val printId: String, val batchId: String,
val printerId: String, val completedAt: Long)
@Serializable data class PrintJobFailedPayload(val printId: String, val batchId: String,
val printerId: String, val attempt: Int, val error: String, val nextRetryAt: Long)
@Serializable data class PrinterStatusChangedPayload(val printerId: String, val previousStatus: String,
val newStatus: String, val detectedAt: Long, val lastSeenAt: Long? = null)
@Serializable data class CatalogItemAvailabilityChangedPayload(val itemId: String, val available: Boolean)
@Serializable data class CashSessionOpenedPayload(val sessionId: String, val openedBy: String,
val openedAt: Long, val openingAmount: Double)
@Serializable data class CashMovementPayload(val movementId: String, val sessionId: String,
val kind: String, val amount: Double, val reason: String, val registeredBy: String)
@Serializable data class CashSessionClosedPayload(val sessionId: String, val closedBy: String,
val closingAmount: Double, val expectedAmount: Double, val difference: Double, val notes: String? = null)
@Serializable data class UserCreatedPayload(val userId: String, val name: String, val role: String,
val pinHash: String, val pinSalt: String)
@Serializable data class AuditActionRecordedPayload(val action: String, val actorUserId: String,
val actorDeviceId: String, val targetType: String? = null, val targetId: String? = null,
val details: Map<String, String> = emptyMap())
