package br.com.aspas.contingencia.navigation
object Routes {
const val LOGIN = "login"
const val SETUP = "setup"
const val MANAGEMENT = "management"
const val TABLES = "tables"
const val ORDER = "order/{tableId}"
const val CHECKOUT = "checkout/{tableId}/{orderId}"
const val PRINTERS = "printers"
const val KITCHEN = "kitchen"
fun order(tableId: String) = "order/$tableId"
fun checkout(tableId: String, orderId: String) = "checkout/$tableId/$orderId"
}
