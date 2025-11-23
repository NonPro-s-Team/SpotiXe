package Components.Layout

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import kotlin.math.max
import kotlin.math.min

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

    fun filter(s: String) = s.filter(Char::isDigit).take(count)

    BoxWithConstraints(
        modifier = Modifier
            .clickable { focusRequester.requestFocus() }
    ) {
        val maxWidth = this.maxWidth

        val minSpace = 4.dp


        val rawBoxSize = maxWidth / (count * 1.6f)
        val adaptiveBoxSize = min(boxSize, rawBoxSize).coerceAtLeast(32.dp)

        val totalBoxWidth = adaptiveBoxSize * count
        val remaining = maxWidth - totalBoxWidth

        val spaceBetween: Dp = if (count > 1) {
            max(minSpace, remaining / (count + 1))
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
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        spaceBetween,
                        Alignment.CenterHorizontally
                    )
                ) {
                    repeat(count) { i ->
                        val ch = otp.value.getOrNull(i)?.toString() ?: ""
                        Box(
                            modifier = Modifier
                                .size(adaptiveBoxSize)
                                .background(Color(0xFF444444), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF444444), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (mask && ch.isNotEmpty()) "*" else ch,
                                fontSize = textSize,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        )
    }
}
