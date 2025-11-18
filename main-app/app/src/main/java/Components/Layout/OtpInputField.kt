package Components.Layout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

@Composable
fun OtpInputField(
    otp: MutableState<String>,
    count: Int = 6,
    mask: Boolean = false,
    onFilled: (String) -> Unit = {},
    boxSize: Dp,
    textSize: TextUnit
) {
    val focusRequester = remember { FocusRequester() }

    fun filter(s: String) = s.filter(Char::isDigit).take(count)

    Box(
        modifier = Modifier
            .clickable { focusRequester.requestFocus() }
    ) {
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    repeat(count) { i ->
                        val ch = otp.value.getOrNull(i)?.toString() ?: ""
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF444444), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF444444), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (mask && ch.isNotEmpty()) "•" else ch,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        )
    }
}

