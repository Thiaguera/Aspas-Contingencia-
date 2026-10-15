package br.com.aspas.contingencia.data.db.dao
import androidx.room.*
import br.com.aspas.contingencia.data.db.entity.*
import kotlinx.coroutines.flow.Flow
@Dao
interface EventDao {
@Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(e: EventEntity): Long
@Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(events: List<EventEntity>): List<Long>
@Query("SELECT EXISTS(SELECT 1 FROM events WHERE eventId = :id)") suspend fun exists(id: String): Boolean
@Query("SELECT EXISTS(SELECT 1 FROM events WHERE type = :type AND json_extract(payload, '$.orderId') = :orderId)") suspend fun hasEventForOrder(type: String, orderId: String): Boolean
@Query("SELECT EXISTS(SELECT 1 FROM events WHERE type = :type AND json_extract(payload, '$.userId') = :userId)") suspend fun hasEventForUser(type: String, userId: String): Boolean
@Query("SELECT * FROM events WHERE synced = 0 ORDER BY hlc ASC LIMIT :limit") suspend fun pending(limit: Int = 500): List<EventEntity>
@Query("SELECT * FROM events WHERE hlc > :since ORDER BY hlc ASC LIMIT :limit") suspend fun eventsSince(since: String, limit: Int): List<EventEntity>
@Query("SELECT * FROM events ORDER BY hlc ASC") fun observeAll(): Flow<List<EventEntity>>
@Query("UPDATE events SET synced = 1 WHERE eventId IN (:ids)") suspend fun markSynced(ids: List<String>)
@Query("SELECT * FROM events ORDER BY hlc ASC") suspend fun all(): List<EventEntity>
@Query("SELECT * FROM events ORDER BY hlc DESC LIMIT :limit") suspend fun latest(limit: Int): List<EventEntity>
@Query("SELECT COUNT() FROM events") suspend fun count(): Int
@Query("SELECT COUNT() FROM events WHERE synced = 0") suspend fun countPending(): Int
@Query("DELETE FROM events WHERE hlc <= :upto AND synced = 1") suspend fun pruneSyncedUpto(upto: String): Int
}
@Dao
interface TableDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(t: TableEntity)
@Query("SELECT * FROM tables ORDER BY number ASC") fun observeAll(): Flow<List<TableEntity>>
@Query("SELECT * FROM tables ORDER BY number ASC") suspend fun listAll(): List<TableEntity>
@Query("SELECT * FROM tables WHERE tableId = :id") suspend fun getById(id: String): TableEntity?
@Query("SELECT * FROM tables WHERE tableId = :id") fun observeById(id: String): Flow<TableEntity?>
@Query("SELECT COUNT(*) FROM tables WHERE status = 'open'") suspend fun countOpen(): Int
}
@Dao
interface OrderDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(o: OrderEntity)
@Query("SELECT * FROM orders WHERE orderId = :id") suspend fun getById(id: String): OrderEntity?
@Query("SELECT * FROM orders WHERE tableId = :tableId AND status != 'closed' ORDER BY openedAt DESC LIMIT 1") suspend fun activeByTable(tableId: String): OrderEntity?
@Query("SELECT * FROM orders WHERE orderId = :id") fun observeById(id: String): Flow<OrderEntity?>
@Query("SELECT * FROM orders WHERE status = 'open'") suspend fun listOpen(): List<OrderEntity>
@Query("SELECT * FROM orders WHERE status = 'open'") fun observeAllOpen(): Flow<List<OrderEntity>>
@Query("SELECT * FROM orders") suspend fun listAll(): List<OrderEntity>
@Query("SELECT * FROM orders WHERE openedAt BETWEEN :from AND :to") suspend fun listBetween(from: Long, to: Long): List<OrderEntity>
@Query("UPDATE orders SET subtotal = :s, serviceFee = :f, total = :t WHERE orderId = :id") suspend fun updateTotals(id: String, s: Double, f: Double, t: Double)
}
@Dao
interface OrderItemDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(i: OrderItemEntity)
@Query("SELECT * FROM order_items WHERE orderId = :orderId ORDER BY addedAt ASC") fun observeByOrder(orderId: String): Flow<List<OrderItemEntity>>
@Query("SELECT * FROM order_items WHERE itemId = :id") suspend fun getById(id: String): OrderItemEntity?
@Query("SELECT * FROM order_items WHERE parentItemId = :parentId") suspend fun childrenOf(parentId: String): List<OrderItemEntity>
@Query("SELECT * FROM order_items WHERE orderId = :orderId") suspend fun listByOrder(orderId: String): List<OrderItemEntity>
@Query("SELECT * FROM order_items WHERE orderId = :orderId AND status = 'active'") suspend fun listActive(orderId: String): List<OrderItemEntity>
@Query("SELECT * FROM order_items") suspend fun listAll(): List<OrderItemEntity>
}
@Dao
interface PaymentDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PaymentEntity)
@Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY registeredAt ASC") fun observeByOrder(orderId: String): Flow<List<PaymentEntity>>
@Query("SELECT * FROM payments WHERE orderId = :orderId") suspend fun listByOrder(orderId: String): List<PaymentEntity>
@Query("SELECT * FROM payments WHERE paymentId = :id") suspend fun getById(id: String): PaymentEntity?
@Query("SELECT * FROM payments WHERE registeredAt BETWEEN :from AND :to") suspend fun listBySessionWindow(from: Long, to: Long): List<PaymentEntity>
@Query("SELECT * FROM payments") suspend fun listAll(): List<PaymentEntity>
}
@Dao
interface LeaseDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(l: LeaseEntity)
@Query("SELECT * FROM leases WHERE leaseId = :id") suspend fun getById(id: String): LeaseEntity?
@Query("SELECT * FROM leases WHERE tableId = :tableId AND status = 'active' LIMIT 1") suspend fun activeByTable(tableId: String): LeaseEntity?
@Query("SELECT * FROM leases WHERE tableId = :tableId AND status = 'active' LIMIT 1") fun observeActiveByTable(tableId: String): Flow<LeaseEntity?>
@Query("SELECT * FROM leases WHERE status = 'active' AND expiresAt <= :now") suspend fun expired(now: Long): List<LeaseEntity>
@Query("UPDATE leases SET status = :status, releasedAt = :at, releasedBy = :by WHERE leaseId = :id") suspend fun markReleased(id: String, status: String, at: Long, by: String?)
}
@Dao
interface CatalogDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertCategory(c: CatalogCategoryEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertItem(i: CatalogItemEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertCategories(l: List<CatalogCategoryEntity>)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertItems(l: List<CatalogItemEntity>)
@Query("SELECT * FROM catalog_categories ORDER BY sortOrder ASC") fun observeCategories(): Flow<List<CatalogCategoryEntity>>
@Query("SELECT * FROM catalog_items WHERE available = 1 ORDER BY sortOrder ASC") fun observeAvailableItems(): Flow<List<CatalogItemEntity>>
@Query("SELECT * FROM catalog_items WHERE itemId = :id") suspend fun getItem(id: String): CatalogItemEntity?
@Query("DELETE FROM catalog_categories") suspend fun wipeCategories()
@Query("DELETE FROM catalog_items") suspend fun wipeItems()
}
@Dao
interface PrinterDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PrinterEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(l: List<PrinterEntity>)
@Query("SELECT * FROM printers ORDER BY printerId ASC") fun observeAll(): Flow<List<PrinterEntity>>
@Query("SELECT * FROM printers") suspend fun listAll(): List<PrinterEntity>
@Query("SELECT * FROM printers WHERE printerId = :id") suspend fun getById(id: String): PrinterEntity?
@Query("UPDATE printers SET lastStatus = :status, lastSeenAt = :at WHERE printerId = :id") suspend fun updateStatus(id: String, status: String, at: Long)
}
@Dao
interface PrintJobDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertBatch(b: PrintBatchEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertJob(j: PrintJobEntity)
@Query("SELECT * FROM print_jobs WHERE status IN ('queued','sending') AND (nextRetryAt IS NULL OR nextRetryAt <= :now) ORDER BY createdAt ASC LIMIT :limit") suspend fun dueJobs(now: Long, limit: Int = 20): List<PrintJobEntity>
@Query("SELECT * FROM print_jobs WHERE printId = :id") suspend fun getById(id: String): PrintJobEntity?
@Query("SELECT * FROM print_batches WHERE batchId = :id") suspend fun getBatch(id: String): PrintBatchEntity?
@Query("SELECT * FROM print_jobs WHERE status = 'failed' ORDER BY createdAt DESC") fun observeFailed(): Flow<List<PrintJobEntity>>
@Query("SELECT * FROM print_jobs WHERE status = 'queued' ORDER BY createdAt ASC") fun observeQueued(): Flow<List<PrintJobEntity>>
@Query("SELECT * FROM print_jobs WHERE printerId = :id ORDER BY createdAt DESC LIMIT 50") fun observeByPrinter(id: String): Flow<List<PrintJobEntity>>
@Query("UPDATE print_jobs SET status = :status, attempts = :a, lastError = :err, nextRetryAt = :retry, completedAt = :done WHERE printId = :id") suspend fun updateStatus(id: String, status: String, a: Int, err: String?, retry: Long?, done: Long?)
}
@Dao
interface UserDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(u: UserEntity)
@Query("SELECT * FROM users WHERE userId = :id") suspend fun getById(id: String): UserEntity?
@Query("SELECT * FROM users WHERE active = 1") suspend fun listActive(): List<UserEntity>
@Query("SELECT * FROM users WHERE active = 1 ORDER BY name ASC") fun observeActive(): Flow<List<UserEntity>>
@Query("UPDATE users SET active = 0 WHERE userId = :id") suspend fun deactivate(id: String)
}
@Dao
interface AuditDao {
@Insert suspend fun insert(e: AuditLogEntity)
@Query("SELECT * FROM audit_log ORDER BY createdAt DESC LIMIT :limit") fun observeRecent(limit: Int = 100): Flow<List<AuditLogEntity>>
}
@Dao
interface SnapshotDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(s: SnapshotEntity)
@Query("SELECT * FROM snapshots ORDER BY createdAt DESC LIMIT 1") suspend fun latest(): SnapshotEntity?
@Query("DELETE FROM snapshots WHERE snapshotId != :keep") suspend fun pruneExcept(keep: String)
}
@Dao
interface CashSessionDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(s: CashSessionEntity)
@Query("SELECT * FROM cash_sessions WHERE status = 'open' LIMIT 1") suspend fun active(): CashSessionEntity?
@Query("SELECT * FROM cash_sessions WHERE status = 'open' LIMIT 1") fun observeActive(): Flow<CashSessionEntity?>
@Query("SELECT * FROM cash_sessions WHERE sessionId = :id") suspend fun getById(id: String): CashSessionEntity?
}
@Dao
interface CashMovementDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(m: CashMovementEntity)
@Query("SELECT * FROM cash_movements WHERE sessionId = :id ORDER BY registeredAt ASC") suspend fun listBySession(id: String): List<CashMovementEntity>
@Query("SELECT * FROM cash_movements WHERE sessionId = :id ORDER BY registeredAt ASC") fun observeBySession(id: String): Flow<List<CashMovementEntity>>
}
@Dao
interface SyncStateDao {
@Query("SELECT * FROM sync_state") fun observeAll(): Flow<List<SyncStateEntity>>
}
@Dao
interface AppliedEventDao {
@Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(marker: AppliedEventEntity): Long
@Query("SELECT EXISTS(SELECT 1 FROM applied_events WHERE eventId = :eventId)") suspend fun isApplied(eventId: String): Boolean
@Query("SELECT COUNT(*) FROM applied_events") suspend fun count(): Int
@Query("DELETE FROM applied_events") suspend fun clear()
}
@Dao
interface ProjectionStateDao {
@Query("SELECT * FROM projection_state WHERE `key` = :key") suspend fun get(key: String = ProjectionStateEntity.KEY): ProjectionStateEntity?
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun put(state: ProjectionStateEntity)
}
