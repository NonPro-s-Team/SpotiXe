package com.example.spotixe.auth.data.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object to provide Retrofit instances with proper configuration
 */
object RetrofitClient {
    
    private const val BASE_URL = "https://api.spotixe.io.vn/"

    // Gson instance with custom configuration
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    
    /**
     * Create OkHttpClient with interceptors
     */
    private fun createOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // Auto-attach JWT
            .addInterceptor(UnauthorizedInterceptor(context)) // Handle 401 Unauthorized
            .addInterceptor(loggingInterceptor) // Log requests/responses
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Create Retrofit instance
     */
    fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Get AuthApiService instance
     */
    fun getAuthApiService(context: Context): AuthApiService {
        return createRetrofit(context).create(AuthApiService::class.java)
    }
    
    /**
     * Get SongApiService instance
     */
    fun getSongApiService(context: Context): com.example.spotixe.api.SongApiService {
        return createRetrofit(context).create(com.example.spotixe.api.SongApiService::class.java)
    }
    
    /**
     * Create a generic API service
     * Usage: val apiService = RetrofitClient.createService<YourApiService>(context)
     */
    inline fun <reified T> createService(context: Context): T {
        return createRetrofit(context).create(T::class.java)
    }
}
