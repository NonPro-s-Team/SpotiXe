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
    modifier: Modifier = Modifier,
    // progress từ ngoài truyền vào (0f..1f), dùng để sync khi KHÔNG kéo
    progress: Float,
    height: Dp,
    activeColor: Color,
    inactiveColor: Color,
    onSeekPreview: ((Float) -> Unit)? = null, // update UI tạm thời (optional)
    onSeekEnd: ((Float) -> Unit)? = null      // commit cuối cùng (seekTo, update VM)
) {
    var barSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var internalProgress by remember { mutableFloatStateOf(progress) }

    // Khi không kéo thì cho internalProgress chạy theo progress từ ngoài
    LaunchedEffect(progress, isDragging) {
        if (!isDragging) {
            internalProgress = progress
        }
    }

    Box(
        modifier
            .height(height)
            .fillMaxWidth()
            .onGloballyPositioned {
                barSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    if (barSize.width <= 0f) return@awaitEachGesture

                    isDragging = true

                    fun updateFromX(x: Float) {
                        val p = (x / barSize.width).coerceIn(0f, 1f)
                        internalProgress = p
                        onSeekPreview?.invoke(p)   // chỉ update UI/VM nhẹ
                    }

                    updateFromX(down.position.x)

                    drag(down.id) { change ->
                        updateFromX(change.position.x)
                        change.consume()
                    }

                    isDragging = false
                    onSeekEnd?.invoke(internalProgress) // chỉ gọi 1 lần ở cuối
                }
            }
            .background(inactiveColor, RoundedCornerShape(percent = 50))
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(internalProgress.coerceIn(0f, 1f))
                .background(activeColor, RoundedCornerShape(percent = 50))
        )
    }
}
