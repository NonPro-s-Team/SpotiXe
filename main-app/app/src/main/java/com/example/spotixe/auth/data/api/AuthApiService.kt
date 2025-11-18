package com.example.spotixe.auth.data.api

import com.example.spotixe.auth.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints
 */
interface AuthApiService {
    
    /**
     * Login with Firebase ID Token
     * 
     * @param firebaseIdToken Firebase ID token (without "Bearer " prefix)
     * @return LoginResponse with JWT token and user info
     */
    @POST("auth/login")
    suspend fun login(
        @Header("Authorization") authorization: String
    ): Response<LoginResponse>
}
