package Components.Bar

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    progress: Float,
    height: Dp,
    activeColor: Color,
    inactiveColor: Color,
    onSeekPreview: ((Float) -> Unit)? = null,
    onSeekEnd: ((Float) -> Unit)? = null
) {
    var barSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    var internalProgress by remember {
        mutableFloatStateOf(progress.coerceIn(0f, 1f))
    }

    LaunchedEffect(progress, isDragging) {
        if (!isDragging) {
            internalProgress = progress.coerceIn(0f, 1f)
        }
    }

    val visualProgress by animateFloatAsState(
        targetValue = internalProgress,
        animationSpec = tween(
            durationMillis = 120,
            easing = LinearOutSlowInEasing
        ),
        label = "scrub-progress"
    )

    Box(
        modifier
            .height(height)
            .fillMaxWidth()
            .onGloballyPositioned { layout ->
                barSize = Size(
                    width = layout.size.width.toFloat(),
                    height = layout.size.height.toFloat()
                )
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()

                    if (barSize.width <= 0f) return@awaitEachGesture
                    isDragging = true

                    // ⭐ NHẤN ĐỂ TUA — chuyển đổi x → progress ngay lập tức
                    fun positionToProgress(x: Float): Float {
                        return (x / barSize.width).coerceIn(0f, 1f)
                    }

                    // Set ngay progress khi user chạm (tap-to-seek)
                    internalProgress = positionToProgress(down.position.x)
                    onSeekPreview?.invoke(internalProgress)

                    val startX = down.position.x
                    val startProgress = internalProgress

                    fun deltaToProgress(deltaX: Float): Float {
                        val delta = deltaX / barSize.width
                        return (startProgress + delta).coerceIn(0f, 1f)
                    }

                    drag(down.id) { change ->
                        val deltaX = change.position.x - startX
                        val newP = deltaToProgress(deltaX)
                        internalProgress = newP
                        onSeekPreview?.invoke(newP)
                        change.consume()
                    }

                    isDragging = false
                    onSeekEnd?.invoke(internalProgress)
                }
            }
            .background(inactiveColor, RoundedCornerShape(percent = 50))
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(visualProgress)
                .background(activeColor, RoundedCornerShape(percent = 50))
        )
    }
}
