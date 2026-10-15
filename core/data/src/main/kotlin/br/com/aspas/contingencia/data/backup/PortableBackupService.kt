package br.com.aspas.contingencia.data.backup

import android.content.Context
import androidx.room.withTransaction
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.DatabaseFactory
import java.io.File

/** Password-wrapped export whose inner image is independently SQLCipher encrypted. */
class PortableBackupService(private val context: Context, private val activeDb: AspasDatabase) {
    data class Verification(val eventCount: Int, val schemaVersion: Int)

    suspend fun export(destination: File, password: CharArray, databasePassphrase: ByteArray) {
        require(password.size >= EncryptedBackup.MIN_PASSWORD_CHARS) { "backup password is too short" }
        require(destination.parentFile?.exists() == true) { "backup destination directory does not exist" }
        require(!destination.exists()) { "refusing to overwrite existing backup" }
        val temp = File.createTempFile("aspas-backup-", ".db", context.cacheDir)
        try {
            val quotedPath = temp.absolutePath.replace("'", "''")
            val keyHex = databasePassphrase.joinToString("") { "%02x".format(it.toInt() and 0xff) }
            // sqlcipher_export copies under the same explicit key while Room holds a
            // transaction, giving a consistent image without VACUUM INTO's ambiguity.
            activeDb.withTransaction {
                val sql = activeDb.openHelper.writableDatabase
                sql.execSQL("ATTACH DATABASE '$quotedPath' AS portable KEY \"x'$keyHex'\"")
                try { sql.execSQL("SELECT sqlcipher_export('portable')") }
                finally { sql.execSQL("DETACH DATABASE portable") }
            }
            val image = temp.readBytes()
            try {
                require(!image.copyOfRange(0, minOf(16, image.size)).contentEquals(SQLITE_HEADER)) {
                    "SQLCipher export unexpectedly has a plaintext SQLite header"
                }
                destination.writeBytes(EncryptedBackup.create(image, databasePassphrase, password))
            } finally { image.fill(0) }
        } finally { deleteWithSidecars(temp) }
    }

    /** Restores only into a brand new isolated name; no projection or printing starts. */
    suspend fun restoreIntoNewDatabase(archive: File, password: CharArray, destinationName: String): Verification {
        require(password.size >= EncryptedBackup.MIN_PASSWORD_CHARS) { "backup password is too short" }
        require(destinationName != AspasDatabase.NAME && destinationName.matches(Regex("[A-Za-z0-9_.-]{1,80}"))) { "invalid isolated destination" }
        require(archive.length() in 1..EncryptedBackup.MAX_ARCHIVE_BYTES) { "invalid backup size" }
        val target = context.getDatabasePath(destinationName)
        require(!hasAnySidecar(target)) { "refusing destination with existing database sidecar" }
        val restored = EncryptedBackup.open(archive.readBytes(), password)
        try {
            require(!restored.databaseImage.copyOfRange(0, minOf(16, restored.databaseImage.size)).contentEquals(SQLITE_HEADER)) {
                "backup contains a plaintext SQLite image"
            }
            target.parentFile?.mkdirs()
            target.writeBytes(restored.databaseImage)
            val opened = DatabaseFactory.create(context, restored.sqlCipherPassphrase, destinationName)
            return try {
                Verification(opened.eventDao().count(), opened.openHelper.writableDatabase.version)
            } finally { opened.close() }
        } catch (t: Throwable) {
            deleteWithSidecars(target)
            throw t
        } finally { restored.close() }
    }

    private fun hasAnySidecar(file: File) = listOf(file, File(file.path + "-wal"), File(file.path + "-shm"), File(file.path + "-journal")).any(File::exists)
    private fun deleteWithSidecars(file: File) = listOf(file, File(file.path + "-wal"), File(file.path + "-shm"), File(file.path + "-journal")).forEach(File::delete)
    private companion object { val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII) }
}
