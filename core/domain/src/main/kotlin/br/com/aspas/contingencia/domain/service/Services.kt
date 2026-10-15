package br.com.aspas.contingencia.domain.service
import br.com.aspas.contingencia.domain.model.*
import kotlinx.coroutines.flow.Flow
interface LeaseManager {
suspend fun acquire(tableId: String, userId: String): AcquireResult
suspend fun renew(tableId: String, userId: String): Boolean
suspend fun release(tableId: String, userId: String, reason: String)
suspend fun forceRelease(tableId: String, managerId: String, reason: String)
fun observeActive(tableId: String): Flow<Lease?>
suspend fun sweepExpired()
sealed interface AcquireResult {
data class Success(val lease: Lease) : AcquireResult
data class Busy(val heldBy: String, val expiresAt: Long) : AcquireResult
data class Failed(val reason: String) : AcquireResult
}
companion object {
const val LEASE_TTL_MS = 90_000L
const val HEARTBEAT_MS = 30_000L
}
}
interface PrintDispatcher {
suspend fun enqueueItem(item: OrderItem, orderId: String): PrintBatch
suspend fun enqueueCancellation(item: OrderItem, orderId: String, reason: String, cancelledBy: String): PrintBatch
suspend fun reprint(printId: String, requestedBy: String): PrintBatch
suspend fun enqueueTest(printerId: String, requestedBy: String): PrintBatch
suspend fun forwardToPeer(printId: String, peerDeviceId: String)
suspend fun processQueue()
}
interface SyncEngine {
suspend fun start()
suspend fun stop()
fun isCaughtUp(): Flow<Boolean>
suspend fun syncWith(peerDeviceId: String)
}
