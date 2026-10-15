package br.com.aspas.contingencia.data.repository

import androidx.room.withTransaction
import br.com.aspas.contingencia.common.Hlc
import br.com.aspas.contingencia.common.HlcGenerator
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.dao.EventDao
import br.com.aspas.contingencia.data.db.entity.EventEntity
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.repository.CloseTableAppendResult
import br.com.aspas.contingencia.domain.repository.EventAppendRequest
import br.com.aspas.contingencia.domain.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val db: AspasDatabase,
    private val dao: EventDao,
    private val hlcGenerator: HlcGenerator,
    @Named("deviceId") private val deviceId: String,
    private val sessionProvider: SessionProvider
) : EventRepository {
    private val lock = Mutex()

    override suspend fun append(type: String, payloadJson: String, userId: String?): EventEnvelope =
        appendBatch(listOf(EventAppendRequest(type, payloadJson, userId))).single()

    override suspend fun appendBatch(requests: List<EventAppendRequest>): List<EventEnvelope> =
        withContext(Dispatchers.IO) {
            require(requests.all { it.type in EventType.ALL }) { "tipo de evento inválido" }
            if (requests.isEmpty()) return@withContext emptyList()
            lock.withLock {
                val events = requests.map { request -> newEnvelope(request) }
                db.withTransaction { dao.insertAll(events.map { it.toEntity(synced = false) }) }
                events
            }
        }

    override suspend fun appendCloseBatch(
        tableId: String,
        orderId: String,
        requests: List<EventAppendRequest>
    ): CloseTableAppendResult = withContext(Dispatchers.IO) {
        require(requests.isNotEmpty()) { "lote de fechamento vazio" }
        require(requests.last().type == EventType.TABLE_CLOSED) { "fechamento deve terminar com table_closed" }
        require(requests.dropLast(1).all { it.type == EventType.PAYMENT_REGISTERED }) {
            "lote de fechamento só aceita pagamentos e table_closed"
        }
        require(requests.all { it.type in EventType.ALL }) { "tipo de evento inválido" }
        lock.withLock {
            db.withTransaction {
                // This guard intentionally reads the event log, not projections: projections may lag.
                if (dao.hasEventForOrder(EventType.TABLE_CLOSED, orderId)) {
                    CloseTableAppendResult(appended = false)
                } else {
                    val events = requests.map { newEnvelope(it) }
                    dao.insertAll(events.map { it.toEntity(synced = false) })
                    CloseTableAppendResult(appended = true, events = events)
                }
            }
        }
    }

    private fun newEnvelope(request: EventAppendRequest): EventEnvelope {
        val hlc = hlcGenerator.next()
        return EventEnvelope(
            Uuid.new(), hlc.encode(), deviceId,
            request.userId ?: sessionProvider.currentUserId(), request.type, request.payloadJson,
            System.currentTimeMillis()
        )
    }

    override suspend fun appendRemote(envelope: EventEnvelope) = withContext(Dispatchers.IO) {
        if (dao.exists(envelope.eventId)) return@withContext
        if (envelope.type !in EventType.ALL) return@withContext
        val remoteHlc = runCatching { Hlc.decode(envelope.hlc) }.getOrNull() ?: return@withContext
        hlcGenerator.observe(remoteHlc)
        dao.insert(envelope.toEntity(synced = true))
    }

    override fun pendingForSync(): Flow<List<EventEnvelope>> =
        dao.observeAll().map { list -> list.filter { !it.synced }.map { it.toEnvelope() } }

    override suspend fun eventsSince(sinceHlc: Hlc?, limit: Int): List<EventEnvelope> = withContext(Dispatchers.IO) {
        val since = sinceHlc?.encode() ?: "0-0-0000"
        dao.eventsSince(since, limit).map { it.toEnvelope() }
    }

    override suspend fun markSynced(eventIds: List<String>) = withContext(Dispatchers.IO) {
        if (eventIds.isNotEmpty()) dao.markSynced(eventIds)
    }

    override suspend fun exists(eventId: String): Boolean = withContext(Dispatchers.IO) { dao.exists(eventId) }

    override fun observeAll(): Flow<EventEnvelope> = flow {
        dao.observeAll().collect { list -> list.forEach { emit(it.toEnvelope()) } }
    }
}

private fun EventEnvelope.toEntity(synced: Boolean) =
    EventEntity(eventId, hlc, deviceId, userId, type, payload, createdAt, synced)
private fun EventEntity.toEnvelope() =
    EventEnvelope(eventId, hlc, deviceId, userId, type, payload, createdAt)
