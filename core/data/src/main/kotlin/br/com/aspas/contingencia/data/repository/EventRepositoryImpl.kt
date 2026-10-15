package br.com.aspas.contingencia.data.repository
import br.com.aspas.contingencia.common.Hlc
import br.com.aspas.contingencia.common.HlcGenerator
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.dao.EventDao
import br.com.aspas.contingencia.data.db.entity.EventEntity
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named
@Singleton
class EventRepositoryImpl @Inject constructor(
private val dao: EventDao,
private val hlcGenerator: HlcGenerator,
@Named("deviceId") private val deviceId: String,
private val sessionProvider: SessionProvider
) : EventRepository {
private val lock = Mutex()
override suspend fun append(type: String, payloadJson: String, userId: String?): EventEnvelope =
withContext(Dispatchers.IO) {
lock.withLock {
val hlc = hlcGenerator.next()
val env = EventEnvelope(Uuid.new(), hlc.encode(), deviceId,
userId ?: sessionProvider.currentUserId(), type, payloadJson, System.currentTimeMillis())
dao.insert(env.toEntity(synced = false))
env
}
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
override suspend fun eventsSince(sinceHlc: Hlc?, limit: Int): List<EventEnvelope> =
withContext(Dispatchers.IO) {
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
private fun EventEnvelope.toEntity(synced: Boolean) = EventEntity(eventId, hlc, deviceId, userId, type, payload, createdAt, synced)
private fun EventEntity.toEnvelope() = EventEnvelope(eventId, hlc, deviceId, userId, type, payload, createdAt)
