package br.com.aspas.contingencia.data.backup

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.fail
import org.junit.Test

class EncryptedBackupTest {
    private val dbKey = ByteArray(32) { it.toByte() }
    private val image = ByteArray(1024) { (it % 251).toByte() }

    @Test fun roundTripKeepsImageAndDbKey() {
        val archive = EncryptedBackup.create(image, dbKey, "correct horse battery staple".toCharArray())
        EncryptedBackup.open(archive, "correct horse battery staple".toCharArray()).use {
            assertArrayEquals(image, it.databaseImage)
            assertArrayEquals(dbKey, it.sqlCipherPassphrase)
        }
    }

    @Test fun wrongPasswordIsRejected() {
        val archive = EncryptedBackup.create(image, dbKey, "good".toCharArray())
        try { EncryptedBackup.open(archive, "bad".toCharArray()); fail("expected rejection") }
        catch (_: EncryptedBackup.BackupException) { }
    }

    @Test fun tamperingIsRejected() {
        val archive = EncryptedBackup.create(image, dbKey, "good".toCharArray())
        archive[archive.lastIndex] = (archive.last().toInt() xor 1).toByte()
        try { EncryptedBackup.open(archive, "good".toCharArray()); fail("expected rejection") }
        catch (_: EncryptedBackup.BackupException) { }
    }
}
