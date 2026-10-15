package br.com.aspas.contingencia.data.projection

import br.com.aspas.contingencia.data.db.AspasDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-local exclusion for physical effects.  The durable projection state is
 * checked while the same lock is held, closing the check-then-send race with a
 * rebuild.  A stale durable rebuild flag intentionally fails closed after a crash.
 */
@Singleton
class MaintenanceGate @Inject constructor(private val db: AspasDatabase) {
    private val mutex = Mutex()

    suspend fun <T> withPhysicalProcessing(block: suspend () -> T): T? = mutex.withLock {
        val state = db.projectionStateDao().get()
        // legacyCheckpointHlc denotes v1 state whose queued physical jobs cannot be
        // proven against an event ledger. It remains quarantined until explicit rebuild.
        if (state?.rebuildInProgress == true || state?.legacyCheckpointHlc != null) null else block()
    }

    suspend fun <T> withExclusiveMaintenance(block: suspend () -> T): T = mutex.withLock { block() }
}
