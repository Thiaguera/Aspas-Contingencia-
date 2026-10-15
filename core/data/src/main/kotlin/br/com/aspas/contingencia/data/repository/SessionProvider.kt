package br.com.aspas.contingencia.data.repository
interface SessionProvider {
fun currentUserId(): String?
fun currentDeviceId(): String
}
