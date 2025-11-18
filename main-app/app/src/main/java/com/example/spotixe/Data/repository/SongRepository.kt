package com.example.spotixe.data.repository

import android.content.Context
import android.util.Log
import com.example.spotixe.api.SongApiService
import com.example.spotixe.auth.data.api.RetrofitClient
import com.example.spotixe.Data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for song-related data operations
 */
class SongRepository(context: Context) {
    
    private val songApiService: SongApiService = RetrofitClient.getSongApiService(context)
    
    companion object {
        private const val TAG = "SongRepository"
    }
    
    /**
     * Fetch all songs from API
     */
    suspend fun getAllSongs(): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching all songs from API...")
            val response = songApiService.getSongs()
            
            if (response.isSuccessful) {
                val songs = response.body() ?: emptyList()
                Log.d(TAG, "Successfully fetched ${songs.size} songs")
                Result.success(songs)
            } else {
                val errorMsg = "API Error: ${response.code()} ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching songs", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch song by ID
     */
    suspend fun getSongById(songId: Long): Result<Song> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching song with ID: $songId")
            val response = songApiService.getSongById(songId)
            
            if (response.isSuccessful) {
                val song = response.body()
                if (song != null) {
                    Log.d(TAG, "Successfully fetched song: ${song.title}")
                    Result.success(song)
                } else {
                    val errorMsg = "Song not found"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorMsg = "API Error: ${response.code()} ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching song by ID", e)
            Result.failure(e)
        }
    }

    /**
     * Fetch artist by ID
     */
    suspend fun getArtistById(artistId: Long): Result<com.example.spotixe.api.ArtistResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching artist with ID: $artistId")
            val response = songApiService.getArtistById(artistId)

            if (response.isSuccessful) {
                val artist = response.body()
                if (artist != null) {
                    Log.d(TAG, "Successfully fetched artist: ${artist.name}")
                    Result.success(artist)
                } else {
                    val errorMsg = "Artist not found"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorMsg = "API Error: ${response.code()} ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artist by ID", e)
            Result.failure(e)
        }
    }
}
