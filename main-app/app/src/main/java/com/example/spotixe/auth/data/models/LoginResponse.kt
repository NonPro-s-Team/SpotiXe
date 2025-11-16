package com.example.spotixe.auth.data.models

import com.google.gson.annotations.SerializedName

/**
 * Login response from backend API
 * POST https://api.spotixe.io.vn/auth/login
 */
data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("token")
    val token: String,
    
    @SerializedName("expiresIn")
    val expiresIn: Int,
    
    @SerializedName("user")
    val user: User
)

/**
 * Error response from backend
 */
data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("details")
    val details: String?
)
