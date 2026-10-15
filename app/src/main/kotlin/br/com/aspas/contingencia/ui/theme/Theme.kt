package br.com.aspas.contingencia.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val AspasGreen = Color(0xFF1B4332)
val AspasGreenLight = Color(0xFF2D6A4F)
val AspasSand = Color(0xFFE9C46A)
val AspasClay = Color(0xFFB08968)
val AspasCream = Color(0xFFFAF3E0)
val AspasInk = Color(0xFF1A1A1A)
val StatusFree = Color(0xFF52B788)
val StatusOpen = Color(0xFFF4A261)
val StatusClosing = Color(0xFF457B9D)
private val Light = lightColorScheme(
primary = AspasGreen, onPrimary = AspasCream,
secondary = AspasSand, tertiary = AspasClay,
background = AspasCream, surface = AspasCream, onSurface = AspasInk)
private val Dark = darkColorScheme(
primary = AspasGreenLight, secondary = AspasSand, tertiary = AspasClay)
@Composable
fun AspasTheme(content: @Composable () -> Unit) {
MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
