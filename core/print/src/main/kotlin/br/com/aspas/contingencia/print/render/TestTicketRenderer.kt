package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.print.driver.EscPosCommands
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TestTicketRenderer @Inject constructor() : TicketRenderer {
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
writeLine(out, TicketLayout.boxedTitle("TESTE DE IMPRESSÃO"))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols("Impressora", ctx.printer.printerId)))
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols("IP", ctx.printer.ip)))
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols("Hora", TicketLayout.formatTime(ctx.printedAt))))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("Acentos: ção ãéíóú"))
writeLine(out, TicketLayout.boxed("Números: 0123456789"))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxedTitle("Se você lê isto, OK"))
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
