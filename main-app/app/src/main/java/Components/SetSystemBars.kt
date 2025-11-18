package Components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetSystemBars() {
    val controller = rememberSystemUiController()
    val dark = Color(0xFF121212)
    SideEffect {
        controller.setSystemBarsColor(dark, darkIcons = false)
        controller.setNavigationBarColor(dark, darkIcons = false)
    }
}