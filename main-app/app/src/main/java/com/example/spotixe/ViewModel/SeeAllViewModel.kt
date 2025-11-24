package com.example.spotixe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotixe.Data.AlbumRepository
import com.example.spotixe.Data.ArtistRepository
import com.example.spotixe.Data.PlaylistRepository
import com.example.spotixe.Data.model.Song
import com.example.spotixe.Pages.Pages.AppMainPages.SeeAllType
import com.example.spotixe.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UI State cho SeeAll screens
 * Chứa dữ liệu cho tất cả loại content (Song, Playlist, Album, Artist)
 */
data class SeeAllUiState(
    val isLoading: Boolean = false,
    val items: List<Any> = emptyList(), // Generic list chứa Song, Playlist, Album, hoặc Artist
    val errorMessage: String? = null
)

/**
 * ViewModel quản lý state cho tất cả "See All" screens
 * Nhận type để biết load dữ liệu nào
 */
class SeeAllViewModel(context: Context) : ViewModel() {

    private val songRepository = SongRepository(context)

    private val _uiState = MutableStateFlow(SeeAllUiState())
    val uiState: StateFlow<SeeAllUiState> = _uiState.asStateFlow()

    /**
     * Load data theo type
     */
    fun loadData(type: SeeAllType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val items: List<Any> = withContext(Dispatchers.IO) {
                    when (type) {
                        SeeAllType.RecentlyPlayed -> {
                            // Load recently played songs từ API
                            val result = songRepository.getAllSongs()
                            result.getOrNull()?.take(50) ?: emptyList()
                        }
                        SeeAllType.Playlists -> {
                            // Load tất cả playlists
                            PlaylistRepository.all
                        }
                        SeeAllType.Albums -> {
                            // Load tất cả albums
                            AlbumRepository.all
                        }
                        SeeAllType.Artists -> {
                            // Load tất cả artists
                            ArtistRepository.all
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = items
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

