package br.com.aspas.contingencia.print
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class RetryPolicy @Inject constructor() {
private val backoff = longArrayOf(5_000, 15_000, 45_000, 120_000, 300_000)
fun nextRetry(attempt: Int): Long {
val idx = (attempt - 1).coerceIn(0, backoff.size - 1)
return System.currentTimeMillis() + backoff[idx]
}
}
