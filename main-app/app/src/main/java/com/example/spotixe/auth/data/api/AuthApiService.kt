package com.example.spotixe.auth.data.api

import com.example.spotixe.auth.data.models.LoginResponse
import com.example.spotixe.auth.data.models.RequestOtpRequest
import com.example.spotixe.auth.data.models.RequestOtpRespone
import com.example.spotixe.auth.data.models.VerifyOtpRequest
import com.example.spotixe.auth.data.models.VerifyOtpRespone
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
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

    @POST("auth/request-otp")
    suspend fun requestOtp(
        @Body request: RequestOtpRequest
    ): Response<RequestOtpRespone>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<VerifyOtpRespone>


}
