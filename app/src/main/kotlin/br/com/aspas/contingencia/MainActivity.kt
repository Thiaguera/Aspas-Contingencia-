package br.com.aspas.contingencia
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import br.com.aspas.contingencia.navigation.AspasNavHost
import br.com.aspas.contingencia.navigation.Routes
import br.com.aspas.contingencia.ui.theme.AspasTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContent {
AspasTheme {
Surface(Modifier.fillMaxSize()) {
AspasNavHost(rememberNavController(), startDestination = Routes.LOGIN)
}
}
}
}
}
