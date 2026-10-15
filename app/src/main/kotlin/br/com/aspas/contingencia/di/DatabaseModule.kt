package br.com.aspas.contingencia.di
import android.content.Context
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
}
