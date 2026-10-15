package br.com.aspas.contingencia.domain.event
import kotlinx.serialization.Serializable
@Serializable
data class EventEnvelope(
val eventId: String, val hlc: String, val deviceId: String,
val userId: String?, val type: String, val payload: String, val createdAt: Long)
