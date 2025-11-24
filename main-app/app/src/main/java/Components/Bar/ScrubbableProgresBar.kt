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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ScrubbableProgressBar
 *
 * @param progress 0f..1f – progress từ ViewModel / player đưa vào (single source of truth)
 * @param onSeekPreview gọi liên tục khi đang kéo (dùng để update UI tạm thời nếu cần)
 * @param onSeekEnd gọi đúng 1 lần khi user buông tay, trả về progress 0f..1f
 */
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

    // progress nội bộ để UI bám theo tay khi kéo
    var internalProgress by remember {
        mutableFloatStateOf(progress.coerceIn(0f, 1f))
    }

    /**
     * 🔁 Đồng bộ từ progress bên ngoài vào internalProgress
     * Chỉ sync khi KHÔNG kéo (isDragging == false) để tránh giật khi đang scrub.
     */
    LaunchedEffect(progress, isDragging) {
        if (!isDragging) {
            internalProgress = progress.coerceIn(0f, 1f)
        }
    }

    /**
     * 🎞 Animation cho phần hiển thị để kéo tới / kéo lui đều mượt.
     * internalProgress = giá trị logic
     * visualProgress  = giá trị vẽ ra (được tween nhẹ)
     */
    val visualProgress by animateFloatAsState(
        targetValue = internalProgress.coerceIn(0f, 1f),
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
            .onGloballyPositioned { layoutCoordinates ->
                barSize = Size(
                    width = layoutCoordinates.size.width.toFloat(),
                    height = layoutCoordinates.size.height.toFloat()
                )
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()

                    if (barSize.width <= 0f) return@awaitEachGesture

                    isDragging = true

                    // Lưu lại vị trí & progress tại thời điểm bắt đầu kéo
                    val startX = down.position.x
                    val startProgress = internalProgress

                    fun clamp(p: Float): Float = p.coerceIn(0f, 1f)

                    fun updateFromDelta(deltaX: Float) {
                        if (barSize.width <= 0f) return
                        val deltaProgress = deltaX / barSize.width
                        val newProgress = clamp(startProgress + deltaProgress)
                        internalProgress = newProgress
                        onSeekPreview?.invoke(newProgress)
                    }

                    // Nếu chỉ tap mà không kéo xa, internalProgress vẫn ~ startProgress
                    updateFromDelta(0f)

                    drag(down.id) { change ->
                        val deltaX = change.position.x - startX
                        updateFromDelta(deltaX)
                        change.consume()
                    }

                    // Buông tay
                    isDragging = false
                    onSeekEnd?.invoke(internalProgress.coerceIn(0f, 1f))
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
