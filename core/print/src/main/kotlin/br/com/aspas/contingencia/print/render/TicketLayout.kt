package br.com.aspas.contingencia.print.render
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
object TicketLayout {
const val TOTAL_COLS = 32
const val MARGIN = 2
const val USABLE = TOTAL_COLS - (MARGIN * 2)
fun center(text: String, width: Int = USABLE): String =
text.take(width).padStart((width + text.length) / 2).padEnd(width)
fun twoCols(left: String, right: String, width: Int = USABLE): String {
val space = width - left.length - right.length
return when {
space <= 0 -> "${left.take((width - right.length - 1).coerceAtLeast(0))} $right"
else -> left + " ".repeat(space) + right }
}
fun border(width: Int = USABLE): String = "+" + "-".repeat(width) + "+"
fun boxed(content: String, width: Int = USABLE): String = "|" + content.take(width).padEnd(width) + "|"
fun boxedTitle(text: String, width: Int = USABLE): String = "|" + center(text, width) + "|"
fun bold(text: String): String = "\u001B\u0045\u0001$text\u001B\u0045\u0000"
fun formatTime(ms: Long): String = SimpleDateFormat("HH:mm", Locale("pt","BR")).format(Date(ms))
fun formatDateTime(ms: Long): String = SimpleDateFormat("dd/MM HH:mm", Locale("pt","BR")).format(Date(ms))
fun formatMoney(value: Double): String = "R " + String.format(Locale("pt","BR"), "%,.2f", value)
fun wrap(text: String, width: Int = USABLE): List<String> {
if (text.isBlank()) return emptyList()
val words = text.split(" ").filter { it.isNotEmpty() }
val lines = mutableListOf<String>()
var current = StringBuilder()
for (w in words) {
if (current.length + w.length + 1 > width) {
lines += current.toString().trim(); current = StringBuilder() }
current.append(w).append(" ")
}
if (current.isNotEmpty()) lines += current.toString().trim()
return lines
}
}
