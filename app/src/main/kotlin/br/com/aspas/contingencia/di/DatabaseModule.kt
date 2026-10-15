package br.com.aspas.contingencia.di
import android.content.Context
import br.com.aspas.contingencia.data.db.dao.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.DatabaseFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
@Provides @Singleton
fun provideJson(): Json = Json {
ignoreUnknownKeys = true
encodeDefaults = true
explicitNulls = false
}
@Provides @Singleton
fun provideDatabase(@ApplicationContext ctx: Context): AspasDatabase {
val mk = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
val prefs = EncryptedSharedPreferences.create(ctx, "aspas_secrets", mk,
EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
val hex = prefs.getString("db_passphrase", null)
val pass = if (hex != null) {
ByteArray(hex.length / 2) { i -> hex.substring(i * 2, i * 2 + 2).toInt(16).toByte() }
} else {
DatabaseFactory.generatePassphrase().also { bytes ->
prefs.edit().putString("db_passphrase",
bytes.joinToString("") { "%02x".format(it) }).apply()
}
}
return DatabaseFactory.create(ctx, pass)
}
@Provides fun provideEventDao(db: AspasDatabase): EventDao = db.eventDao()
@Provides fun provideTableDao(db: AspasDatabase): TableDao = db.tableDao()
@Provides fun provideOrderDao(db: AspasDatabase): OrderDao = db.orderDao()
@Provides fun provideOrderItemDao(db: AspasDatabase): OrderItemDao = db.orderItemDao()
@Provides fun providePaymentDao(db: AspasDatabase): PaymentDao = db.paymentDao()
@Provides fun provideLeaseDao(db: AspasDatabase): LeaseDao = db.leaseDao()
@Provides fun provideCatalogDao(db: AspasDatabase): CatalogDao = db.catalogDao()
@Provides fun providePrinterDao(db: AspasDatabase): PrinterDao = db.printerDao()
@Provides fun providePrintJobDao(db: AspasDatabase): PrintJobDao = db.printJobDao()
@Provides fun provideUserDao(db: AspasDatabase): UserDao = db.userDao()
@Provides fun provideAuditDao(db: AspasDatabase): AuditDao = db.auditDao()
@Provides fun provideSnapshotDao(db: AspasDatabase): SnapshotDao = db.snapshotDao()
@Provides fun provideCashSessionDao(db: AspasDatabase): CashSessionDao = db.cashSessionDao()
@Provides fun provideCashMovementDao(db: AspasDatabase): CashMovementDao = db.cashMovementDao()
@Provides fun provideSyncStateDao(db: AspasDatabase): SyncStateDao = db.syncStateDao()
@Provides fun provideAppliedEventDao(db: AspasDatabase): AppliedEventDao = db.appliedEventDao()
@Provides fun provideProjectionStateDao(db: AspasDatabase): ProjectionStateDao = db.projectionStateDao()
}
