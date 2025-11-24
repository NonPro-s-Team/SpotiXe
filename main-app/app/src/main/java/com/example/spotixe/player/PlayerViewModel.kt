package com.example.spotixe.player

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.spotixe.Data.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// Import old Song for backward compatibility
import com.example.spotixe.Data.Song as OldSong

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "PlayerViewModel"
    }
    
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(0)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private var progressJob: Job? = null
    
    init {
        initializeController(application)
    }
    
    private fun initializeController(application: Application) {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, MusicPlayerService::class.java)
        )
        
        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                mediaController = controllerFuture?.get()
                setupPlayerListener()
                Log.d(TAG, "MediaController connected")
            },
            MoreExecutors.directExecutor()
        )
    }
    
    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startProgressUpdate()
                } else {
                    stopProgressUpdate()
                }
                Log.d(TAG, "Playing state changed: $isPlaying")
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSongFromMediaItem(mediaItem)
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        _duration.value = mediaController?.duration ?: 0L
                        Log.d(TAG, "Player ready, duration: ${_duration.value}")
                    }
                    Player.STATE_ENDED -> {
                        _isPlaying.value = false
                        stopProgressUpdate()
                        Log.d(TAG, "Playback ended")
                    }
                    else -> {}
                }
            }
        })
    }
    
    fun playSong(song: Song) {
        _currentSong.value = song
        
        if (_playlist.value.isEmpty() || _playlist.value.none { it.songId == song.songId }) {
            _playlist.value = listOf(song)
            _currentIndex.value = 0
        } else {
            val index = _playlist.value.indexOfFirst { it.songId == song.songId }
            if (index >= 0) {
                _currentIndex.value = index
            }
        }

        val mediaItem = MediaItem.Builder()
            .setUri(song.audioFileUrl)
            .setMediaId(song.songId.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist("Artist")
                    .setArtworkUri(android.net.Uri.parse(song.coverImageUrl))
                    .build()
            )
            .build()
        
        mediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        
        Log.d(TAG, "Playing song: ${song.title}, playlist size: ${_playlist.value.size}, index: ${_currentIndex.value}")
    }
    
    fun playFromList(index: Int) {
        if (index < 0 || index >= _playlist.value.size) {
            Log.w(TAG, "Invalid index: $index")
            return
        }

        _currentIndex.value = index
        val song = _playlist.value[index]
        playSong(song)

        Log.d(TAG, "Playing song from list: ${song.title} at index $index")
    }

    fun playFromList(songs: List<Song>, index: Int) {
        if (songs.isEmpty()) {
            Log.w(TAG, "Empty song list")
            return
        }

        if (index < 0 || index >= songs.size) {
            Log.w(TAG, "Invalid index: $index for list size: ${songs.size}")
            return
        }

        _playlist.value = songs
        _currentIndex.value = index
        playSong(songs[index])

        Log.d(TAG, "Playing from new playlist: ${songs[index].title} at index $index of ${songs.size} songs")
    }

    @Deprecated("Use playFromList with new Song model", ReplaceWith("playFromList(songs, index)"))
    fun playFromOldList(songs: List<OldSong>, index: Int) {
        if (songs.isEmpty()) {
            Log.w(TAG, "Empty song list (old format)")
            return
        }

        if (index < 0 || index >= songs.size) {
            Log.w(TAG, "Invalid index: $index for list size: ${songs.size}")
            return
        }

        val oldSong = songs[index]
        Log.w(TAG, "Attempting to play old format song: ${oldSong.title}")
        Log.w(TAG, "Old song format doesn't have audioFileUrl - cannot play actual audio")

        val mockSong = Song(
            songId = oldSong.id.toLongOrNull() ?: 0L,
            title = oldSong.title,
            duration = 180,
            releaseDate = oldSong.year,
            audioFileUrl = "https://example.com/mock.mp3",
            coverImageUrl = "https://example.com/mock.jpg",
            genre = null,
            artistId = null,
            albumId = null,
            artistName = null,
            isActive = 1,
            createdAt = null,
            updatedAt = null,
            deletedAt = null
        )

        _currentSong.value = mockSong
        Log.d(TAG, "Created mock song for playback: ${oldSong.title}")
    }

    fun playNext() {
        val playlist = _playlist.value
        if (playlist.isEmpty()) return

        val nextIndex = (_currentIndex.value + 1) % playlist.size
        playFromList(nextIndex)
    }

    fun playPrevious() {
        val playlist = _playlist.value
        if (playlist.isEmpty()) return

        val prevIndex = if (_currentIndex.value > 0) {
            _currentIndex.value - 1
        } else {
            playlist.size - 1
        }
        playFromList(prevIndex)
    }

    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    
    fun seekTo(percent: Float) {
        val targetPosition = (percent * (_duration.value)).toLong()
        mediaController?.seekTo(targetPosition)

        _currentPosition.value = targetPosition
        val duration = _duration.value
        if (duration > 0) {
            _progress.value = targetPosition.toFloat() / duration
        }
    }


    fun seekToPosition(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    private var wasPlayingBeforeSeek = false

    fun pauseForSeeking() {
        mediaController?.let {
            wasPlayingBeforeSeek = it.isPlaying
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun resumeAfterSeeking() {
        if (wasPlayingBeforeSeek) {
            mediaController?.play()
        }
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && _isPlaying.value) {
                mediaController?.let { controller ->
                    val position = controller.currentPosition
                    val duration = controller.duration
                    
                    _currentPosition.value = position
                    if (duration > 0) {
                        _progress.value = position.toFloat() / duration
                    }
                }
                delay(100)
            }
        }
    }
    
    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }
    
    private fun updateCurrentSongFromMediaItem(mediaItem: MediaItem?) {
        mediaItem?.mediaId?.toLongOrNull()?.let { songId ->
            val newIndex = _playlist.value.indexOfFirst { it.songId == songId }
            if (newIndex != -1) {
                _currentIndex.value = newIndex
                _currentSong.value = _playlist.value[newIndex]
                Log.d(TAG, "Media item transitioned to: ${_currentSong.value?.title} at index $newIndex")
            } else {
                Log.w(TAG, "Media item with ID $songId not found in current playlist")
            }
        }
    }
    
    fun setRepeatMode(mode: Int) {
        _repeatMode.value = mode
        mediaController?.repeatMode = when (mode) {
            1 -> Player.REPEAT_MODE_ONE
            2 -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        Log.d(TAG, "Repeat mode set to: $mode")
    }

    fun reset() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
        _currentSong.value = null
        _playlist.value = emptyList()
        _currentIndex.value = 0
        _isPlaying.value = false
        _progress.value = 0f
        _currentPosition.value = 0L
        _duration.value = 0L
        stopProgressUpdate()
        Log.d(TAG, "PlayerViewModel reset")
    }

    override fun onCleared() {
        mediaController?.release()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        progressJob?.cancel()
        super.onCleared()
    }
}
