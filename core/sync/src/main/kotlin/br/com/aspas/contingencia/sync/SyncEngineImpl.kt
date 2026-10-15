package br.com.aspas.contingencia.sync
import br.com.aspas.contingencia.domain.service.SyncEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SyncEngineImpl @Inject constructor() : SyncEngine {
override suspend fun start() { /* TODO implementar mDNS+Ktor */ }
override suspend fun stop() { }
override fun isCaughtUp(): Flow<Boolean> = flowOf(true)
override suspend fun syncWith(peerDeviceId: String) { }
}
