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

/**
 * PlayerViewModel with Media3 ExoPlayer integration
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "PlayerViewModel"
    }
    
    // Media Controller
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    // Current song state
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()
    
    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    // Progress (0.0 to 1.0)
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()
    
    // Current position in milliseconds
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    // Duration in milliseconds
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    // Current playlist
    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    // Current index in playlist
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Repeat mode: 0 = no repeat, 1 = repeat once, 2 = repeat all
    private val _repeatMode = MutableStateFlow(0)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    // Progress update job
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
    
    /**
     * Play a song from API
     */
    fun playSong(song: Song) {
        _currentSong.value = song
        
        // Tự động tạo playlist với bài hát này nếu chưa có trong playlist
        // hoặc nếu playlist rỗng
        if (_playlist.value.isEmpty() || _playlist.value.none { it.songId == song.songId }) {
            _playlist.value = listOf(song)
            _currentIndex.value = 0
        } else {
            // Nếu bài hát đã có trong playlist, cập nhật index
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
                    .setArtist("Artist") // TODO: Add artist info when available
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
    
    /**
     * Play a song from the playlist by index
     */
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

    /**
     * Play a song from a new playlist
     */
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

    /**
     * Play a song from the old Song class list (backward compatibility)
     * Note: Old Song uses mock data and doesn't have actual audio files
     * @deprecated Use the new Song model from API instead
     */
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

        // Create a mock Song with placeholder data
        // In production, you should convert old songs to new format or fetch from API
        val mockSong = Song(
            songId = oldSong.id.toLongOrNull() ?: 0L,
            title = oldSong.title,
            duration = 180, // mock duration
            releaseDate = oldSong.year,
            audioFileUrl = "https://example.com/mock.mp3", // placeholder
            coverImageUrl = "https://example.com/mock.jpg", // placeholder
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

        // Don't actually play since we don't have real audio file
        // playSong(mockSong)
    }

    /**
     * Play next song in the playlist
     */
    fun playNext() {
        val playlist = _playlist.value
        if (playlist.isEmpty()) return

        val nextIndex = (_currentIndex.value + 1) % playlist.size
        playFromList(nextIndex)
    }

    /**
     * Play previous song in the playlist
     */
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

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    
    /**
     * Seek to position (0.0 to 1.0)
     */
    fun seekTo(percent: Float) {
        val targetPosition = (percent * (_duration.value)).toLong()
        mediaController?.seekTo(targetPosition)
    }
    
    /**
     * Seek to specific position in milliseconds
     */
    fun seekToPosition(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    /**
     * Pause for seeking (save previous state)
     */
    private var wasPlayingBeforeSeek = false

    fun pauseForSeeking() {
        mediaController?.let {
            wasPlayingBeforeSeek = it.isPlaying
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    /**
     * Resume after seeking if it was playing before
     */
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
                delay(100) // Update every 100ms
            }
        }
    }
    
    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }
    
    private fun updateCurrentSongFromMediaItem(mediaItem: MediaItem?) {
        // Update current song if needed when media item changes
        mediaItem?.mediaMetadata?.let { metadata ->
            Log.d(TAG, "Media item changed: ${metadata.title}")
        }
    }
    
    /**
     * Set repeat mode
     * 0 = no repeat, 1 = repeat once, 2 = repeat all
     */
    fun setRepeatMode(mode: Int) {
        _repeatMode.value = mode
        mediaController?.repeatMode = when (mode) {
            1 -> Player.REPEAT_MODE_ONE
            2 -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        Log.d(TAG, "Repeat mode set to: $mode")
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
