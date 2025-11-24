package Components

import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable

fun Modifier.noDoubleClick(
    interval: Long = 1000L,   // khoảng thời gian tối thiểu giữa 2 lần click (ms)
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by rememberSaveable { mutableLongStateOf(0L) }

    this.clickable {
        val now = SystemClock.uptimeMillis()
        if (now - lastClickTime >= interval) {
            lastClickTime = now
            onClick()
        }
    }
}
