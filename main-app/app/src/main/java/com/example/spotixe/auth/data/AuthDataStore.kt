package com.example.spotixe.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension function to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spotixe_auth")

/**
 * AuthDataStore handles persistent storage of authentication data
 * using Jetpack DataStore (SharedPreferences alternative)
 */
class AuthDataStore(private val context: Context) {

    companion object {
        // Keys for DataStore
        private val JWT_TOKEN_KEY = stringPreferencesKey("spotixe_jwt")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_USERNAME_KEY = stringPreferencesKey("user_username")
        private val USER_AVATAR_URL_KEY = stringPreferencesKey("user_avatar_url")
        private val USER_FIREBASE_UID_KEY = stringPreferencesKey("user_firebase_uid")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
    }

    /**
     * Save JWT token to DataStore
     */
    suspend fun saveJwtToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    /**
     * Get JWT token as Flow (reactive)
     */
    fun getJwtToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }
    }

    /**
     * Get JWT token as suspend function (one-time read)
     */
    suspend fun getJwtTokenOnce(): String? {
        var token: String? = null
        context.dataStore.data.map { preferences ->
            token = preferences[JWT_TOKEN_KEY]
        }.collect { }
        return token
    }

    /**
     * Save complete user information
     */
    suspend fun saveUser(
        userId: Long,
        email: String?,
        username: String?,
        avatarUrl: String?,
        firebaseUid: String?,
        phone: String?
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            email?.let { preferences[USER_EMAIL_KEY] = it }
            username?.let { preferences[USER_USERNAME_KEY] = it }
            avatarUrl?.let { preferences[USER_AVATAR_URL_KEY] = it }
            firebaseUid?.let { preferences[USER_FIREBASE_UID_KEY] = it }
            phone?.let { preferences[USER_PHONE_KEY] = it }
        }
    }

    /**
     * Get user data as Flow
     */
    fun getUserData(): Flow<UserData?> {
        return context.dataStore.data.map { preferences ->
            val userId = preferences[USER_ID_KEY]
            if (userId != null) {
                UserData(
                    userId = userId,
                    email = preferences[USER_EMAIL_KEY],
                    username = preferences[USER_USERNAME_KEY],
                    avatarUrl = preferences[USER_AVATAR_URL_KEY],
                    firebaseUid = preferences[USER_FIREBASE_UID_KEY],
                    phone = preferences[USER_PHONE_KEY]
                )
            } else {
                null
            }
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY] != null && preferences[USER_ID_KEY] != null
        }
    }

    /**
     * Clear all authentication data (logout)
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Data class to hold user information
     */
    data class UserData(
        val userId: Long,
        val email: String?,
        val username: String?,
        val avatarUrl: String?,
        val firebaseUid: String?,
        val phone: String?
    )
}
