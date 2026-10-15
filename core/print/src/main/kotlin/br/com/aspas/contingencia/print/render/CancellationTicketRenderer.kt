package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.print.driver.EscPosCommands
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CancellationTicketRenderer @Inject constructor() : TicketRenderer {
override fun render(ctx: TicketContext): ByteArray {
val out = ByteArrayOutputStream()
out.write(EscPosCommands.INIT)
out.write(EscPosCommands.ALIGN_CENTER)
out.write(EscPosCommands.FONT_DOUBLE_BOTH)
out.write(TicketLayout.bold(ctx.printer.headerLabel).toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
out.write(EscPosCommands.FONT_NORMAL)
out.write(EscPosCommands.ALIGN_LEFT)
writeLine(out, TicketLayout.center("*** CANCELAMENTO ***"))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols(
"MESA ${ctx.tableNumber.toString().padStart(2, '0')}",
ctx.waiterName.take(14).uppercase())))
writeLine(out, TicketLayout.border())
out.write(EscPosCommands.FONT_DOUBLE_H)
TicketLayout.wrap("${ctx.orderItem.quantity}x ${ctx.orderItem.nameSnapshot}",
TicketLayout.USABLE).forEach { l -> writeLine(out, TicketLayout.boxed(l)) }
out.write(EscPosCommands.FONT_NORMAL)
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("MOTIVO:"))
TicketLayout.wrap(ctx.cancelReason ?: "—", TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) }
ctx.cancelledBy?.let { writeLine(out, TicketLayout.boxed("Por: $it")) }
writeLine(out, TicketLayout.border())
out.write(EscPosCommands.ALIGN_LEFT)
repeat(4) { out.write(EscPosCommands.FEED) }
out.write(EscPosCommands.CUT_FULL)
return out.toByteArray()
}
private fun writeLine(out: ByteArrayOutputStream, line: String) {
out.write(line.toByteArray(Charsets.ISO_8859_1)); out.write(EscPosCommands.FEED)
}
}
