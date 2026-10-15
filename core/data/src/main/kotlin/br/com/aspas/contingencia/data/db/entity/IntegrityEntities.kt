package br.com.aspas.contingencia.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Durable idempotency ledger. A row is written in the same Room transaction as
 * the projection mutations, so a crash can leave neither mutation nor marker.
 */
@Entity(
    tableName = "applied_events",
    indices = [Index("appliedAt"), Index("mode")]
)
data class AppliedEventEntity(
    @PrimaryKey val eventId: String,
    val eventHlc: String,
    val appliedAt: Long,
    val mode: String
)

/** One-row operational state; also makes a maintenance/rebuild visible after restart. */
@Entity(tableName = "projection_state")
data class ProjectionStateEntity(
    @PrimaryKey val key: String = KEY,
    val rebuildInProgress: Boolean,
    val legacyCheckpointHlc: String?,
    val updatedAt: Long
) {
    companion object { const val KEY = "default" }
}
