package com.example.spotixe.auth.data.models

import com.google.gson.annotations.SerializedName

/**
 * User data model matching backend response
 */
data class User(
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("phone")
    val phone: String?,
    
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    
    @SerializedName("firebaseUid")
    val firebaseUid: String?
)
