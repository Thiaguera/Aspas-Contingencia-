package br.com.aspas.contingencia.domain.repository

import br.com.aspas.contingencia.common.Hlc
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.model.*
import kotlinx.coroutines.flow.Flow

/** A typed request prevents callers from accidentally separating a type from its JSON payload. */
data class EventAppendRequest(
    val type: String,
    val payloadJson: String,
    val userId: String? = null
)

data class CloseTableAppendResult(
    val appended: Boolean,
    val events: List<EventEnvelope> = emptyList()
)

interface EventRepository {
    suspend fun append(type: String, payloadJson: String, userId: String? = null): EventEnvelope

    /** Persists every event or none. Projection remains an independent asynchronous consumer. */
    suspend fun appendBatch(requests: List<EventAppendRequest>): List<EventEnvelope>

    /**
     * Atomically records payment(s) and TABLE_CLOSED. A durable TABLE_CLOSED for the order makes
     * retries no-ops even if the projections have not caught up after a crash.
     */
    suspend fun appendCloseBatch(
        tableId: String,
        orderId: String,
        requests: List<EventAppendRequest>
    ): CloseTableAppendResult

    suspend fun appendRemote(envelope: EventEnvelope)
    fun pendingForSync(): Flow<List<EventEnvelope>>
    suspend fun eventsSince(sinceHlc: Hlc?, limit: Int = 500): List<EventEnvelope>
    suspend fun markSynced(eventIds: List<String>)
    suspend fun exists(eventId: String): Boolean
    fun observeAll(): Flow<EventEnvelope>
}
interface TableRepository {
    fun observeAll(): Flow<List<Table>>
    fun observeById(id: String): Flow<Table?>
    suspend fun getById(id: String): Table?
}
interface OrderRepository {
    suspend fun activeByTable(tableId: String): Order?
    fun observeById(orderId: String): Flow<Order?>
    fun observeItems(orderId: String): Flow<List<OrderItem>>
    fun observeAllOpen(): Flow<List<Order>>
}
interface PrinterRepository {
    fun observeAll(): Flow<List<Printer>>
    suspend fun getById(id: String): Printer?
    suspend fun updateStatus(id: String, status: String, at: Long)
}
interface CatalogRepository {
    fun observeCategories(): Flow<List<CatalogCategory>>
    fun observeAvailableItems(): Flow<List<CatalogItem>>
    suspend fun getItem(id: String): CatalogItem?
}
interface UserRepository {
    fun observeActive(): Flow<List<User>>
    suspend fun getById(id: String): User?
    suspend fun listActive(): List<User>
}
interface SyncStateRepository {
    fun observeAll(): Flow<List<SyncState>>
}
