package Components.Layout

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun OtpInputField(
    otp: MutableState<String>,
    count: Int = 6,
    mask: Boolean = false,
    onFilled: (String) -> Unit = {},
    boxSize: Dp = 48.dp,
    textSize: TextUnit = 20.sp
) {
    val focusRequester = remember { FocusRequester() }

    // trạng thái focus của toàn bộ OTP field
    val (isFocused, setFocused) = remember { mutableStateOf(false) }

    // animation nháy cho dấu “|”
    val infiniteTransition = rememberInfiniteTransition(label = "cursor-blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor-alpha"
    )

    fun filter(s: String) = s.filter(Char::isDigit).take(count)

    BoxWithConstraints(
        modifier = Modifier
            .clickable { focusRequester.requestFocus() } // bấm đâu cũng focus vào OTP
    ) {
        val maxWidth = this.maxWidth

        val minSpace = 4.dp

        // Kích thước ô tự co theo width, nhưng không vượt quá boxSize, không nhỏ hơn 32.dp
        val rawBoxSize = maxWidth / (count * 1.6f)
        val adaptiveBoxSize = rawBoxSize
            .coerceAtMost(boxSize)
            .coerceAtLeast(32.dp)

        val totalBoxWidth = adaptiveBoxSize * count
        val remaining = maxWidth - totalBoxWidth

        val spaceBetween: Dp = if (count > 1) {
            // khoảng cách tối thiểu là minSpace, còn lại chia đều
            (remaining / (count + 1)).coerceAtLeast(minSpace)
        } else {
            0.dp
        }

        BasicTextField(
            value = otp.value,
            onValueChange = {
                val v = filter(it)
                otp.value = v
                if (v.length == count) onFilled(v)
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .onFocusChanged { state ->
                    setFocused(state.isFocused)
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        spaceBetween,
                        Alignment.CenterHorizontally
                    )
                ) {
                    val text = otp.value
                    repeat(count) { i ->
                        val ch = text.getOrNull(i)?.toString() ?: ""

                        // ô hiện tại nơi cursor đứng
                        val isCursorPosition =
                            isFocused && (
                                    (text.length == i && text.length < count) ||        // nhập chưa đủ
                                            (text.length == count && i == count - 1)           // đủ rồi thì nháy ở ô cuối
                                    )

                        Box(
                            modifier = Modifier
                                .size(adaptiveBoxSize)
                                .background(Color(0xFF444444), RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (isCursorPosition) Color(0xFF1DB954) else Color(0xFF444444),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                ch.isNotEmpty() -> {
                                    Text(
                                        text = if (mask) "*" else ch,
                                        fontSize = textSize,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }

                                isCursorPosition -> {
                                    // dấu nháy “|” nhấp nháy
                                    Text(
                                        text = "|",
                                        fontSize = textSize,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = cursorAlpha)
                                    )
                                }

                                else -> {
                                    // ô trống, không hiển thị gì
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
