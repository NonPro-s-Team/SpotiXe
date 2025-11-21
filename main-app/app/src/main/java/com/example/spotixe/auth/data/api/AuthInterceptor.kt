package com.example.spotixe.auth.data.api

import android.content.Context
import com.example.spotixe.auth.data.AuthDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    
    private val authDataStore = AuthDataStore(context)
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip adding JWT for auth/login endpoint (it uses Firebase token)
        if (originalRequest.url.encodedPath.contains("/auth/login")) {
            return chain.proceed(originalRequest)
        }
        
        // Get JWT token from DataStore
        val token = runBlocking {
            authDataStore.getJwtToken().first()
        }
        
        // If no token, proceed without modification
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Add Authorization header with JWT token
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(newRequest)
    }
}
