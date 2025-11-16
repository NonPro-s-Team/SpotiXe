package com.example.spotixe.auth.examples

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotixe.auth.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.*

/**
 * Example: How to create an authenticated API service
 * 
 * This example shows how to:
 * 1. Define an API interface with Retrofit
 * 2. Create the service using RetrofitClient
 * 3. Make API calls that automatically include JWT token
 * 4. Handle responses in ViewModel
 */

// ============================================
// STEP 1: Define your API interface
// ============================================

/**
 * Example API service for song operations
 * All endpoints automatically include: Authorization: Bearer {jwt}
 */
interface SongApiService {
    
    /**
     * Get all songs (authenticated)
     */
    @GET("songs")
    suspend fun getAllSongs(): Response<SongListResponse>
    
    /**
     * Get song by ID
     */
    @GET("songs/{id}")
    suspend fun getSongById(@Path("id") songId: String): Response<SongDetailResponse>
    
    /**
     * Add song to favorites (requires authentication)
     */
    @POST("songs/{id}/favorite")
    suspend fun favoriteSong(@Path("id") songId: String): Response<Unit>
    
    /**
     * Remove from favorites
     */
    @DELETE("songs/{id}/favorite")
    suspend fun unfavoriteSong(@Path("id") songId: String): Response<Unit>
    
    /**
     * Get user's favorite songs
     */
    @GET("user/favorites")
    suspend fun getFavoriteSongs(): Response<SongListResponse>
    
    /**
     * Create a new playlist
     */
    @POST("playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequest): Response<PlaylistResponse>
    
    /**
     * Get user's playlists
     */
    @GET("user/playlists")
    suspend fun getMyPlaylists(): Response<PlaylistListResponse>
    
    /**
     * Update user profile
     */
    @PUT("user/profile")
    suspend fun updateProfile(@Body profile: UpdateProfileRequest): Response<UserProfileResponse>
}

// ============================================
// STEP 2: Define data models
// ============================================

data class SongListResponse(
    val success: Boolean,
    val songs: List<SongItem>
)

data class SongItem(
    val id: String,
    val title: String,
    val artist: String,
    val albumArt: String?,
    val duration: Int,
    val isFavorite: Boolean
)

data class SongDetailResponse(
    val success: Boolean,
    val song: SongItem
)

data class CreatePlaylistRequest(
    val name: String,
    val description: String?,
    val isPublic: Boolean
)

data class PlaylistResponse(
    val success: Boolean,
    val playlist: PlaylistItem
)

data class PlaylistItem(
    val id: String,
    val name: String,
    val description: String?,
    val coverUrl: String?,
    val songCount: Int,
    val isPublic: Boolean
)

data class PlaylistListResponse(
    val success: Boolean,
    val playlists: List<PlaylistItem>
)

data class UpdateProfileRequest(
    val username: String?,
    val bio: String?,
    val avatarUrl: String?
)

data class UserProfileResponse(
    val success: Boolean,
    val user: UserProfile
)

data class UserProfile(
    val userId: Long,
    val email: String?,
    val username: String?,
    val bio: String?,
    val avatarUrl: String?
)

// ============================================
// STEP 3: Create ViewModel to use the API
// ============================================

class SongViewModel(application: Application) : AndroidViewModel(application) {
    
    // Create API service instance (JWT is auto-attached!)
    private val songApi = RetrofitClient.createService<SongApiService>(application)
    
    // UI State
    private val _songs = MutableStateFlow<List<SongItem>>(emptyList())
    val songs: StateFlow<List<SongItem>> = _songs
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    /**
     * Load all songs
     * JWT token is automatically attached by AuthInterceptor
     */
    fun loadAllSongs() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                // Make API call - JWT is auto-attached!
                val response = songApi.getAllSongs()
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    if (data.success) {
                        _songs.value = data.songs
                    } else {
                        _error.value = "Failed to load songs"
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
    
    /**
     * Add song to favorites
     */
    fun favoriteSong(songId: String) {
        viewModelScope.launch {
            try {
                val response = songApi.favoriteSong(songId)
                
                if (response.isSuccessful) {
                    // Update local state
                    _songs.value = _songs.value.map { song ->
                        if (song.id == songId) {
                            song.copy(isFavorite = true)
                        } else {
                            song
                        }
                    }
                } else {
                    _error.value = "Failed to favorite song"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    /**
     * Remove song from favorites
     */
    fun unfavoriteSong(songId: String) {
        viewModelScope.launch {
            try {
                val response = songApi.unfavoriteSong(songId)
                
                if (response.isSuccessful) {
                    _songs.value = _songs.value.map { song ->
                        if (song.id == songId) {
                            song.copy(isFavorite = false)
                        } else {
                            song
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    /**
     * Load user's favorite songs
     */
    fun loadFavoriteSongs() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = songApi.getFavoriteSongs()
                
                if (response.isSuccessful && response.body() != null) {
                    _songs.value = response.body()!!.songs
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}

// ============================================
// STEP 4: Use in Composable UI
// ============================================

/**
 * Example Composable using the authenticated API
 */
/*
@Composable
fun SongListScreen(viewModel: SongViewModel = viewModel()) {
    val songs by viewModel.songs.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadAllSongs()  // Automatically uses JWT!
    }
    
    Column {
        if (loading) {
            CircularProgressIndicator()
        }
        
        error?.let { errorMsg ->
            Text("Error: $errorMsg", color = Color.Red)
        }
        
        LazyColumn {
            items(songs) { song ->
                SongItem(
                    song = song,
                    onFavoriteClick = {
                        if (song.isFavorite) {
                            viewModel.unfavoriteSong(song.id)
                        } else {
                            viewModel.favoriteSong(song.id)
                        }
                    }
                )
            }
        }
    }
}
*/

// ============================================
// USAGE SUMMARY
// ============================================

/**
 * HOW TO USE AUTHENTICATED APIS:
 * 
 * 1. Define your API interface:
 *    interface YourApiService {
 *        @GET("endpoint")
 *        suspend fun getData(): Response<YourModel>
 *    }
 * 
 * 2. Create service instance:
 *    val api = RetrofitClient.createService<YourApiService>(context)
 * 
 * 3. Make API calls:
 *    val response = api.getData()
 *    
 * 4. JWT token is AUTOMATICALLY attached to all requests!
 *    You don't need to manually add Authorization header.
 *    
 * 5. The AuthInterceptor handles everything:
 *    - Reads JWT from DataStore
 *    - Adds "Authorization: Bearer {jwt}" header
 *    - Skips auth/login endpoint
 *    
 * 6. Example request headers:
 *    GET https://api.spotixe.io.vn/songs
 *    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *    Content-Type: application/json
 *    
 * That's it! Your API calls are automatically authenticated.
 */
