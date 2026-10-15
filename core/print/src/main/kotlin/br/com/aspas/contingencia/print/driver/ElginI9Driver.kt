package br.com.aspas.contingencia.print.driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
class ElginI9Driver(private val host: String, private val port: Int = 9100,
private val timeoutMs: Int = 3000) {
private var socket: Socket? = null
suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
runCatching {
val s = Socket()
s.connect(InetSocketAddress(host, port), timeoutMs)
s.soTimeout = timeoutMs
socket = s; true
}.getOrDefault(false)
}
suspend fun send(bytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
runCatching {
val s = socket ?: return@runCatching false
s.getOutputStream().apply { write(bytes); flush() }; true
}.getOrDefault(false)
}
suspend fun close() = withContext(Dispatchers.IO) {
runCatching { socket?.close() }; socket = null
}
suspend fun isOnline(): Boolean = withContext(Dispatchers.IO) {
runCatching {
Socket().use { s -> s.connect(InetSocketAddress(host, port), timeoutMs); true }
}.getOrDefault(false)
}
}
