package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.print.driver.EscPosCommands
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ItemTicketRenderer @Inject constructor() : TicketRenderer {
override fun render(ctx: TicketContext): ByteArray {
val out = ByteArrayOutputStream()
out.write(EscPosCommands.INIT)
out.write(EscPosCommands.ALIGN_CENTER)
out.write(EscPosCommands.FONT_DOUBLE_BOTH)
out.write(TicketLayout.bold(ctx.printer.headerLabel).toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
out.write(EscPosCommands.FONT_NORMAL)
out.write(EscPosCommands.ALIGN_LEFT)
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols(
"MESA ${ctx.tableNumber.toString().padStart(2, '0')}",
ctx.waiterName.take(14).uppercase())))
writeLine(out, TicketLayout.boxed("Hora: ${TicketLayout.formatDateTime(ctx.printedAt)}"))
ctx.orderNote?.takeIf { it.isNotBlank() }?.let { note ->
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("*** OBSERVAÇÃO DA MESA ***"))
TicketLayout.wrap(note, TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) }
}
writeLine(out, TicketLayout.border())
val item = ctx.orderItem
val qty = if (item.quantity > 1) "${item.quantity}x " else ""
val weight = item.weightG?.let { " (${it}g)" } ?: ""
out.write(EscPosCommands.FONT_DOUBLE_H)
out.write(EscPosCommands.BOLD_ON)
TicketLayout.wrap("$qty${item.nameSnapshot}$weight", TicketLayout.USABLE).forEach { l ->
writeLine(out, TicketLayout.boxed(l)) }
out.write(EscPosCommands.BOLD_OFF)
out.write(EscPosCommands.FONT_NORMAL)
item.modifiers.forEach { (k, v) ->
if (v.isNotBlank()) {
TicketLayout.wrap("${labelize(k)}: $v", TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) } } }
if (ctx.includedChildren.isNotEmpty()) {
writeLine(out, TicketLayout.border())
ctx.includedChildren.groupBy { it.includedKind ?: "incluso" }.forEach { (kind, list) ->
writeLine(out, TicketLayout.boxed("INCLUSO · ${labelize(kind).uppercase()}"))
list.forEach { c ->
TicketLayout.wrap("• ${c.nameSnapshot}", TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) } } } }
if (ctx.extras.isNotEmpty()) {
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("EXTRAS COBRADOS"))
ctx.extras.forEach { ex ->
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols(
"• ${ex.nameSnapshot}",
TicketLayout.formatMoney(ex.priceSnapshot * ex.quantity)))) } }
writeLine(out, TicketLayout.border())
if (ctx.isReprint) {
out.write(EscPosCommands.ALIGN_CENTER)
out.write(TicketLayout.bold("*** REIMPRESSÃO ***").toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
}
out.write(EscPosCommands.ALIGN_LEFT)
repeat(4) { out.write(EscPosCommands.FEED) }
out.write(EscPosCommands.CUT_FULL)
return out.toByteArray()
}
private fun writeLine(out: ByteArrayOutputStream, line: String) {
out.write(line.toByteArray(Charsets.ISO_8859_1)); out.write(EscPosCommands.FEED)
}
private fun labelize(k: String) = when (k) {
"ponto" -> "Ponto"; "porcao" -> "Porção"; "preparo_peixe" -> "Preparo"
"observacao" -> "Obs"; "guarnicao" -> "Guarnição"
"acompanhamento" -> "Acompanhamento"; "incluso" -> "Incluso"
"free" -> "Item livre"; else -> k.replaceFirstChar { it.uppercase() } }
}
