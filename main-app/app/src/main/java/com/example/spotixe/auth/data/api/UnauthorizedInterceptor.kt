package com.example.spotixe.auth.data.api

import android.content.Context
import android.util.Log
import com.example.spotixe.auth.utils.UnauthorizedEventManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to handle 401 Unauthorized responses
 * When account is disabled (isActive = 0), server returns 401
 */
class UnauthorizedInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Check if response is 401 Unauthorized
        if (response.code == 401) {
            Log.w("UnauthorizedInterceptor", "Received 401 Unauthorized - Account may be disabled")

            // Notify the app about unauthorized access
            // This will trigger the dialog and logout process
            UnauthorizedEventManager.triggerUnauthorizedEvent(context)
        }

        return response
    }
}

