// Components/Bar/ScrubbableProgressBar.kt
package Components.Bar

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScrubbableProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekStart: (() -> Unit)? = null,
    onSeekEnd:   (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    activeColor: Color = Color(0xFF1DB954),
    inactiveColor: Color = Color.Black.copy(alpha = 0.2f)
) {
    var widthPx by remember { mutableStateOf(0f) }

    Box(
        modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)               // hit-area lớn
            .height(height)
            .onGloballyPositioned { widthPx = it.size.width.toFloat() }
            .pointerInput(widthPx) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    if (widthPx > 0f) {
                        onSeekStart?.invoke()
                        onSeek((down.position.x / widthPx).coerceIn(0f, 1f))
                    }
                    drag(down.id) { change ->
                        if (widthPx > 0f) {
                            val p = (change.position.x / widthPx).coerceIn(0f, 1f)
                            onSeek(p)
                        }
                        change.consume()            // nuốt pointer để không rơi lên cha
                    }
                    onSeekEnd?.invoke()
                }
            }
            .background(inactiveColor, RoundedCornerShape(percent = 50))
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(activeColor, RoundedCornerShape(percent = 50))
        )
    }
}