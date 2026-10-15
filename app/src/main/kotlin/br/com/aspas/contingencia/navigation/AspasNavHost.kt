package br.com.aspas.contingencia.navigation
import androidx.compose.runtime.Composable
import br.com.aspas.contingencia.feature.setup.SetupScreen
import br.com.aspas.contingencia.feature.management.ManagementScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.aspas.contingencia.feature.checkout.CheckoutScreen
import br.com.aspas.contingencia.feature.kitchen.KitchenScreen
import br.com.aspas.contingencia.feature.login.LoginScreen
import br.com.aspas.contingencia.feature.order.OrderScreen
import br.com.aspas.contingencia.feature.printers.PrintersScreen
import br.com.aspas.contingencia.feature.tables.TablesScreen
@Composable
fun AspasNavHost(nav: NavHostController, startDestination: String = Routes.LOGIN) {
NavHost(nav, startDestination = startDestination) {
composable(Routes.LOGIN) {
LoginScreen(onSuccess = {
nav.navigate(Routes.SETUP) { popUpTo(Routes.LOGIN) { inclusive = true } } })
}
composable(Routes.SETUP) {
SetupScreen(onReady = { nav.navigate(Routes.TABLES) { popUpTo(Routes.SETUP) { inclusive = true } } })
}
composable(Routes.MANAGEMENT) { ManagementScreen(onBack = { nav.popBackStack() }) }
composable(Routes.TABLES) {
TablesScreen(
onOpen = { tableId -> nav.navigate(Routes.order(tableId)) },
onManagement = { nav.navigate(Routes.MANAGEMENT) },
onLogout = { nav.navigate(Routes.LOGIN) { popUpTo(Routes.TABLES) { inclusive = true } } },
onPrinters = { nav.navigate(Routes.PRINTERS) },
onKitchen = { nav.navigate(Routes.KITCHEN) })
}
composable(Routes.ORDER) {
OrderScreen(
onBack = { nav.popBackStack() },
onCheckout = { tableId, orderId -> nav.navigate(Routes.checkout(tableId, orderId)) })
}
composable(Routes.CHECKOUT) {
CheckoutScreen(
onBack = { nav.popBackStack() },
onClosed = { nav.popBackStack(Routes.TABLES, inclusive = false) })
}
composable(Routes.PRINTERS) { PrintersScreen(onBack = { nav.popBackStack() }) }
composable(Routes.KITCHEN) { KitchenScreen(onBack = { nav.popBackStack() }) }
}
}
