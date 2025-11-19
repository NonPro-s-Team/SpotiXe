package com.example.spotixe.Data.model

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("songId")
    val songId: Long,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("duration")
    val duration: Int, // in seconds
    
    @SerializedName("releaseDate")
    val releaseDate: String?,
    
    @SerializedName("audioFileUrl")
    val audioFileUrl: String,
    
    @SerializedName("coverImageUrl")
    val coverImageUrl: String,
    
    @SerializedName("genre")
    val genre: String?,
    
    @SerializedName("artistId")
    val artistId: Long?,

    @SerializedName("artistName")
    val artistName: String?,
    
    @SerializedName("albumId")
    val albumId: Long?,
    
    @SerializedName("isActive")
    val isActive: Int,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?,
    
    @SerializedName("deletedAt")
    val deletedAt: String?
) {
    // Helper function to format duration as MM:SS
    fun getFormattedDuration(): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
