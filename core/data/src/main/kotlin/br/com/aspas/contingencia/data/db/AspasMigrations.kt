package br.com.aspas.contingencia.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 kept no durable application ledger. Existing v1 projection tables are
 * authoritative at upgrade time, so every existing event is checkpointed rather
 * than replayed. This checkpoint can include queued physical work which cannot
 * be proven applied; startup therefore quarantines physical processing whenever
 * legacyCheckpointHlc is present. An operator must reconcile and explicitly
 * rebuild before printing resumes; the migration never silently claims replay.
 */
object AspasMigrations {
    val V1_TO_V2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `applied_events` (
                    `eventId` TEXT NOT NULL,
                    `eventHlc` TEXT NOT NULL,
                    `appliedAt` INTEGER NOT NULL,
                    `mode` TEXT NOT NULL,
                    PRIMARY KEY(`eventId`)
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_applied_events_appliedAt` ON `applied_events` (`appliedAt`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_applied_events_mode` ON `applied_events` (`mode`)")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `projection_state` (
                    `key` TEXT NOT NULL,
                    `rebuildInProgress` INTEGER NOT NULL,
                    `legacyCheckpointHlc` TEXT,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`key`)
                )
            """.trimIndent())
            // INSERT OR IGNORE preserves an interrupted migration retry safely.
            db.execSQL("""
                INSERT OR IGNORE INTO applied_events(eventId, eventHlc, appliedAt, mode)
                SELECT eventId, hlc, createdAt, 'legacy_checkpoint' FROM events
            """.trimIndent())
            db.execSQL("""
                INSERT OR REPLACE INTO projection_state(`key`, rebuildInProgress, legacyCheckpointHlc, updatedAt)
                SELECT 'default', 0, MAX(hlc), CAST(strftime('%s','now') AS INTEGER) * 1000 FROM events
            """.trimIndent())
        }
    }
}
