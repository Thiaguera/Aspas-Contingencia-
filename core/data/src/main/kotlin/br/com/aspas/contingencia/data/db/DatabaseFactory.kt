package br.com.aspas.contingencia.data.db
import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.security.SecureRandom
object DatabaseFactory {
fun create(context: Context, passphrase: ByteArray, name: String = AspasDatabase.NAME): AspasDatabase {
System.loadLibrary("sqlcipher")
val factory = SupportOpenHelperFactory(passphrase)
return Room.databaseBuilder(context, AspasDatabase::class.java, name)
.openHelperFactory(factory)
.addMigrations(AspasMigrations.V1_TO_V2)
.build()
}
fun generatePassphrase(): ByteArray {
val bytes = ByteArray(32); SecureRandom().nextBytes(bytes); return bytes
}
}
