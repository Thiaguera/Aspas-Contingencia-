package br.com.aspas.contingencia.data.db
import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.aspas.contingencia.data.db.dao.*
import br.com.aspas.contingencia.data.db.entity.*
@Database(
entities = [
EventEntity::class, TableEntity::class, OrderEntity::class, OrderItemEntity::class,
PaymentEntity::class, LeaseEntity::class, CatalogCategoryEntity::class,
CatalogItemEntity::class, PrinterEntity::class, PrintBatchEntity::class,
PrintJobEntity::class, UserEntity::class, DeviceEntity::class, SyncStateEntity::class,
AuditLogEntity::class, SnapshotEntity::class, CashSessionEntity::class,
CashMovementEntity::class, AppliedEventEntity::class, ProjectionStateEntity::class],
version = 2,
exportSchema = false)
abstract class AspasDatabase : RoomDatabase() {
abstract fun eventDao(): EventDao
abstract fun tableDao(): TableDao
abstract fun orderDao(): OrderDao
abstract fun orderItemDao(): OrderItemDao
abstract fun paymentDao(): PaymentDao
abstract fun leaseDao(): LeaseDao
abstract fun catalogDao(): CatalogDao
abstract fun printerDao(): PrinterDao
abstract fun printJobDao(): PrintJobDao
abstract fun userDao(): UserDao
abstract fun auditDao(): AuditDao
abstract fun snapshotDao(): SnapshotDao
abstract fun cashSessionDao(): CashSessionDao
abstract fun cashMovementDao(): CashMovementDao
abstract fun syncStateDao(): SyncStateDao
abstract fun appliedEventDao(): AppliedEventDao
abstract fun projectionStateDao(): ProjectionStateDao
companion object { const val NAME = "aspas_contingencia.db" }
}
