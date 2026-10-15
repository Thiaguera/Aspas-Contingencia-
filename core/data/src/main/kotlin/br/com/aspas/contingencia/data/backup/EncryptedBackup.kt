package br.com.aspas.contingencia.data.backup

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

/** Password-encrypted, bounded portable envelope. It only accepts SQLCipher DB bytes. */
object EncryptedBackup {
    private val MAGIC = byteArrayOf('A'.code.toByte(), 'S'.code.toByte(), 'P'.code.toByte(), 'B'.code.toByte())
    private const val FORMAT = 1
    private const val SALT_BYTES = 16
    private const val IV_BYTES = 12
    private const val KEY_BITS = 256
    private const val ITERATIONS = 310_000
    const val MAX_DATABASE_BYTES = 256L * 1024 * 1024
    const val MAX_ARCHIVE_BYTES = MAX_DATABASE_BYTES + 1024 * 1024
    const val MIN_PASSWORD_CHARS = 12

    class BackupException(message: String, cause: Throwable? = null) : Exception(message, cause)

    /** Plaintext only exists in process memory. The DB image itself remains SQLCipher-encrypted. */
    fun create(databaseImage: ByteArray, sqlCipherPassphrase: ByteArray, password: CharArray): ByteArray {
        require(databaseImage.isNotEmpty() && databaseImage.size <= MAX_DATABASE_BYTES) { "database size outside backup limit" }
        require(sqlCipherPassphrase.size in 16..128) { "invalid database key" }
        val payload = ByteArrayOutputStream(databaseImage.size + sqlCipherPassphrase.size + 8)
        payload.write(intBytes(sqlCipherPassphrase.size))
        payload.write(sqlCipherPassphrase)
        payload.write(databaseImage)
        val salt = random(SALT_BYTES); val iv = random(IV_BYTES)
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key(password, salt), GCMParameterSpec(128, iv))
            val ciphertext = cipher.doFinal(payload.toByteArray())
            ByteArrayOutputStream(MAGIC.size + 1 + SALT_BYTES + IV_BYTES + ciphertext.size).apply {
                write(MAGIC); write(FORMAT); write(salt); write(iv); write(ciphertext)
            }.toByteArray()
        } finally {
            payload.reset(); salt.fill(0); iv.fill(0)
        }
    }

    /** Authenticates fully before returning an image; wrong password/tampering never writes a destination. */
    fun open(archive: ByteArray, password: CharArray): RestoredBackup {
        if (archive.size !in (MAGIC.size + 1 + SALT_BYTES + IV_BYTES + 16)..MAX_ARCHIVE_BYTES) throw BackupException("invalid backup size")
        if (!archive.copyOfRange(0, MAGIC.size).contentEquals(MAGIC) || archive[MAGIC.size].toInt() != FORMAT) throw BackupException("unsupported backup format")
        val saltAt = MAGIC.size + 1; val ivAt = saltAt + SALT_BYTES; val ciphertextAt = ivAt + IV_BYTES
        val salt = archive.copyOfRange(saltAt, ivAt); val iv = archive.copyOfRange(ivAt, ciphertextAt)
        val plaintext = try {
            Cipher.getInstance("AES/GCM/NoPadding").run {
                init(Cipher.DECRYPT_MODE, key(password, salt), GCMParameterSpec(128, iv))
                doFinal(archive, ciphertextAt, archive.size - ciphertextAt)
            }
        } catch (e: AEADBadTagException) { throw BackupException("wrong password or tampered backup", e) }
          catch (e: Exception) { throw BackupException("backup cannot be authenticated", e) }
        try {
            if (plaintext.size < 5) throw BackupException("invalid backup payload")
            val keyLength = readInt(plaintext, 0)
            if (keyLength !in 16..128 || plaintext.size - 4 - keyLength !in 1..MAX_DATABASE_BYTES.toInt()) throw BackupException("invalid backup payload")
            return RestoredBackup(plaintext.copyOfRange(4, 4 + keyLength), plaintext.copyOfRange(4 + keyLength, plaintext.size))
        } finally { plaintext.fill(0); salt.fill(0); iv.fill(0) }
    }

    data class RestoredBackup(val sqlCipherPassphrase: ByteArray, val databaseImage: ByteArray) : AutoCloseable {
        override fun close() { sqlCipherPassphrase.fill(0); databaseImage.fill(0) }
    }
    private fun key(password: CharArray, salt: ByteArray) = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        .generateSecret(PBEKeySpec(password, salt, ITERATIONS, KEY_BITS)).encoded.let { javax.crypto.spec.SecretKeySpec(it, "AES") }
    private fun random(size: Int) = ByteArray(size).also { SecureRandom().nextBytes(it) }
    private fun intBytes(value: Int) = byteArrayOf((value ushr 24).toByte(), (value ushr 16).toByte(), (value ushr 8).toByte(), value.toByte())
    private fun readInt(bytes: ByteArray, at: Int) = ((bytes[at].toInt() and 255) shl 24) or ((bytes[at + 1].toInt() and 255) shl 16) or ((bytes[at + 2].toInt() and 255) shl 8) or (bytes[at + 3].toInt() and 255)
}
