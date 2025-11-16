package com.example.spotixe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotixe.Data.model.Song
import com.example.spotixe.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing song data and UI state
 */
class SongViewModel(context: Context) : ViewModel() {
    
    private val repository = SongRepository(context)
    
    // State for all songs
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadSongs()
    }
    
    /**
     * Load all songs from API
     */
    fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getAllSongs()
                .onSuccess { songList ->
                    _songs.value = songList
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Unknown error occurred"
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Refresh songs
     */
    fun refreshSongs() {
        loadSongs()
    }
    
    /**
     * Get song by ID
     */
    fun getSongById(songId: Long, onResult: (Song?) -> Unit) {
        viewModelScope.launch {
            repository.getSongById(songId)
                .onSuccess { song ->
                    onResult(song)
                }
                .onFailure {
                    onResult(null)
                }
        }
    }

    /**
     * Get artist by ID
     */
    fun getArtistById(artistId: Long, onResult: (com.example.spotixe.api.ArtistResponse?) -> Unit) {
        viewModelScope.launch {
            repository.getArtistById(artistId)
                .onSuccess { artist ->
                    onResult(artist)
                }
                .onFailure {
                    onResult(null)
                }
        }
    }
}
