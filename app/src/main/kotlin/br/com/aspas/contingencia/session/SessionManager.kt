package br.com.aspas.contingencia.session
import br.com.aspas.contingencia.data.repository.SessionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named
@Singleton
class SessionManager @Inject constructor(@Named("deviceId") private val deviceId: String) : SessionProvider {
data class Session(val userId: String, val userName: String, val role: String, val loginAt: Long)
private val _session = MutableStateFlow<Session?>(null)
val session: StateFlow<Session?> = _session.asStateFlow()
override fun currentUserId(): String? = _session.value?.userId
override fun currentDeviceId(): String = deviceId
val currentRole: String? get() = _session.value?.role
fun login(userId: String, userName: String, role: String) {
_session.value = Session(userId, userName, role, System.currentTimeMillis())
}
fun logout() { _session.value = null }
}
