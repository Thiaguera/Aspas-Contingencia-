package br.com.aspas.contingencia.core.lease
import kotlinx.serialization.encodeToString
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.TableLeaseAcquiredPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseExpiredPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseForcedPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseReleasedPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseRenewedPayload
import br.com.aspas.contingencia.domain.model.Lease
import br.com.aspas.contingencia.domain.repository.EventRepository
import br.com.aspas.contingencia.domain.service.LeaseManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class LeaseManagerImpl @Inject constructor(
private val db: AspasDatabase,
private val eventRepository: EventRepository,
private val json: Json
) : LeaseManager {
override suspend fun acquire(tableId: String, userId: String): LeaseManager.AcquireResult {
val current = db.leaseDao().activeByTable(tableId)
if (current != null && current.expiresAt > System.currentTimeMillis()) {
return LeaseManager.AcquireResult.Busy(current.userId, current.expiresAt)
}
val leaseId = Uuid.new()
val now = System.currentTimeMillis()
val expiresAt = now + LeaseManager.LEASE_TTL_MS
val payload = TableLeaseAcquiredPayload(leaseId, tableId, userId, "self", expiresAt)
eventRepository.append(EventType.TABLE_LEASE_ACQUIRED,
json.encodeToString(TableLeaseAcquiredPayload.serializer(), payload), userId)
return LeaseManager.AcquireResult.Success(Lease(leaseId, tableId, userId, "self",
now, now, expiresAt, "active"))
}
override suspend fun renew(tableId: String, userId: String): Boolean {
val lease = db.leaseDao().activeByTable(tableId) ?: return false
if (lease.userId != userId) return false
val now = System.currentTimeMillis()
val expires = now + LeaseManager.LEASE_TTL_MS
eventRepository.append(EventType.TABLE_LEASE_RENEWED,
json.encodeToString(TableLeaseRenewedPayload.serializer(),
TableLeaseRenewedPayload(lease.leaseId, now, expires)), userId)
return true
}
override suspend fun release(tableId: String, userId: String, reason: String) {
val lease = db.leaseDao().activeByTable(tableId) ?: return
eventRepository.append(EventType.TABLE_LEASE_RELEASED,
json.encodeToString(TableLeaseReleasedPayload.serializer(),
TableLeaseReleasedPayload(lease.leaseId, userId, reason)), userId)
}
override suspend fun forceRelease(tableId: String, managerId: String, reason: String) {
val lease = db.leaseDao().activeByTable(tableId) ?: return
eventRepository.append(EventType.TABLE_LEASE_FORCED,
json.encodeToString(TableLeaseForcedPayload.serializer(),
TableLeaseForcedPayload(lease.leaseId, tableId, managerId, reason)), managerId)
}
override fun observeActive(tableId: String): Flow<Lease?> =
db.leaseDao().observeActiveByTable(tableId).map {
it?.let { Lease(it.leaseId, it.tableId, it.userId, it.deviceId,
it.acquiredAt, it.renewedAt, it.expiresAt, it.status) }
}
override suspend fun sweepExpired() {
val now = System.currentTimeMillis()
db.leaseDao().expired(now).forEach {
eventRepository.append(EventType.TABLE_LEASE_EXPIRED,
json.encodeToString(TableLeaseExpiredPayload.serializer(),
TableLeaseExpiredPayload(it.leaseId, it.tableId, now)), null)
db.leaseDao().markReleased(it.leaseId, "expired", now, null)
}
}
}
