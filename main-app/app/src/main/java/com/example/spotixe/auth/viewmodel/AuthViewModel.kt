package com.example.spotixe.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotixe.auth.data.AuthDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Simple ViewModel to check authentication state
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authDataStore = AuthDataStore(application)
    
    /**
     * Check if user is logged in
     */
    val isLoggedIn: StateFlow<Boolean> = authDataStore.isLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
