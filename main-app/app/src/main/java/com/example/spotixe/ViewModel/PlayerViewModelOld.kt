package com.example.spotixe.player.old

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotixe.Data.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val queue: List<Song> = emptyList(),
    val index: Int = -1,
    val isPlaying: Boolean = false,
    val positionSec: Int = 0,
    val durationSec: Int = 286
) {
    val current: Song? get() = queue.getOrNull(index)
    val progress: Float get() = if (durationSec == 0) 0f else positionSec.toFloat() / durationSec
}

class PlayerViewModelOld : ViewModel() {
    private val _ui = MutableStateFlow(PlayerUiState())
    val ui: StateFlow<PlayerUiState> = _ui

    private var ticker: Job? = null
    private var wasPlayingBeforeSeek = false

    /** Play 1 bài trong 1 danh sách (queue). */
    fun playFromList(list: List<Song>, startIndex: Int) {
        if (list.isEmpty() || startIndex !in list.indices) return
        _ui.value = PlayerUiState(
            queue = list,
            index = startIndex,
            isPlaying = true,
            positionSec = 0
        )
        startTicker()
    }

    /** Play 1 bài với queue gợi ý. Nếu queue không chứa bài -> chèn lên đầu. */
    fun play(song: Song, queueHint: List<Song> = emptyList()) {
        val q = if (queueHint.any { it.id == song.id }) queueHint
        else listOf(song) + queueHint.filterNot { it.id == song.id }
        val idx = q.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playFromList(q, idx)
    }

    fun toggle() {
        val s = _ui.value
        val playing = !s.isPlaying
        _ui.value = s.copy(isPlaying = playing)
        if (playing) startTicker() else stopTicker()
    }

    /** Gọi khi bắt đầu kéo tua (pause tạm thời nếu đang play). */
    fun beginSeek() {
        wasPlayingBeforeSeek = _ui.value.isPlaying
        if (wasPlayingBeforeSeek) {
            _ui.value = _ui.value.copy(isPlaying = false)
            stopTicker()
        }
    }

    /** Tua tới phần trăm (0f..1f). */
    fun seekTo(percent: Float) {
        val dur = _ui.value.durationSec
        val sec = (percent.coerceIn(0f, 1f) * dur).toInt()
        _ui.value = _ui.value.copy(positionSec = sec)
    }

    /** Gọi khi thả tay sau khi tua (resume nếu lúc đầu đang play). */
    fun endSeek() {
        if (wasPlayingBeforeSeek) {
            _ui.value = _ui.value.copy(isPlaying = true)
            startTicker()
        }
    }

    fun next() {
        val s = _ui.value
        val nextIdx = (s.index + 1).takeIf { it < s.queue.size } ?: return
        _ui.value = s.copy(index = nextIdx, positionSec = 0, isPlaying = true)
    }

    fun prev() {
        val s = _ui.value
        val prevIdx = (s.index - 1).takeIf { it >= 0 } ?: return
        _ui.value = s.copy(index = prevIdx, positionSec = 0, isPlaying = true)
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val st = _ui.value
                if (st.isPlaying && st.current != null) {
                    val next = (st.positionSec + 1).coerceAtMost(st.durationSec)
                    _ui.value = st.copy(
                        positionSec = next,
                        isPlaying = next < st.durationSec
                    )
                }
            }
        }
    }

    private fun stopTicker() { ticker?.cancel(); ticker = null }
}

@Composable
fun rememberPlayerVMActivity(): PlayerViewModelOld {
    val owner = LocalContext.current as ViewModelStoreOwner
    return viewModel(owner) // scope Activity
}