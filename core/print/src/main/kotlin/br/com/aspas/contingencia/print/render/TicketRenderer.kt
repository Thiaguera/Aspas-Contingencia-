package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.domain.model.OrderItem
import br.com.aspas.contingencia.domain.model.Printer
interface TicketRenderer { fun render(context: TicketContext): ByteArray }
data class TicketContext(
val printer: Printer, val tableNumber: Int, val waiterName: String,
val orderItem: OrderItem, val orderNote: String? = null,
val includedChildren: List<OrderItem> = emptyList(),
val extras: List<OrderItem> = emptyList(),
val kind: TicketKind, val cancelReason: String? = null,
val cancelledBy: String? = null, val isReprint: Boolean = false,
val printedAt: Long = System.currentTimeMillis())
enum class TicketKind { ITEM, CANCELLATION, REPRINT, TEST }
