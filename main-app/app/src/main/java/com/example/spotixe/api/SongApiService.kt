package com.example.spotixe.api

import com.example.spotixe.Data.model.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SongApiService {
    
    @GET("api/songs")
    suspend fun getSongs(): Response<List<Song>>
    
    @GET("api/songs/{id}")
    suspend fun getSongById(@Path("id") songId: Long): Response<Song>

    @GET("api/artists/{id}")
    suspend fun getArtistById(@Path("id") artistId: Long): Response<ArtistResponse>

//    @GET("api/albums/{id}")
//    suspend fun getAlbumById(@Path("id") albumId: Long): Response
}

// Data class for Artist API response
data class ArtistResponse(
    val artistId: Long,
    val name: String,
    val bio: String?,
    val profileImageUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)
