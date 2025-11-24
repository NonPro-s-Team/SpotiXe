package com.example.spotixe.auth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotixe.auth.data.AuthDataStore
import com.example.spotixe.auth.data.models.VerifyOtpRespone
import com.example.spotixe.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Simple ViewModel to check authentication state
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authDataStore = AuthDataStore(application)
    private val authRepository = AuthRepository(application)

    // Khởi tạo 2 biến để kiểm tra state hiện tại của call api request otp
    private val _otpState = MutableStateFlow<String?>(null)
    val otpState: StateFlow<String?> = _otpState

    private val _otpErrorMessage = MutableStateFlow<String?>(null)
    val otpErrorMessage: StateFlow<String?> = _otpErrorMessage

    // verify otp
    private val _verifyState = MutableStateFlow<VerifyOtpRespone?>(null)
    val verifyState = _verifyState.asStateFlow()


    /**
     * Check if user is logged in
     */
    val isLoggedIn: StateFlow<Boolean> = authDataStore.isLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun requestOtp(email: String) {
        viewModelScope.launch {
            val result = authRepository.requestOtp(email)

            result.onSuccess {
                _otpState.value = "success"
                _otpErrorMessage.value = null
            }.onFailure { e ->
                _otpState.value = "error"
                _otpErrorMessage.value = e.message ?: "Unknown error occurred"
            }
        }
    }

    fun clearOtpError() {
        _otpErrorMessage.value = null
        _otpState.value = null
    }

    fun verifyOtp(email: String, otp: String, username: String?) {
        viewModelScope.launch {
            val result = authRepository.verifyOtp(email, otp, username)

            result.onSuccess { response ->
                _verifyState.value = response
            }.onFailure { e ->
                _verifyState.value = null
                Log.e("AuthVM", "Verify OTP failed", e)
            }
        }
    }

    /**
     * Save JWT token and user data from VerifyOtpResponse to DataStore
     */
    fun saveAuthDataFromOtp(response: VerifyOtpRespone) {
        viewModelScope.launch {
            try {
                // Save JWT token
                authDataStore.saveJwtToken(response.token)

                // Save user data
                authDataStore.saveUser(
                    userId = response.user.userId.toLong(),
                    email = response.user.email,
                    username = response.user.username,
                    avatarUrl = null,
                    firebaseUid = null,
                    phone = null
                )

                Log.d("AuthVM", "Auth data saved successfully from OTP verification")
            } catch (e: Exception) {
                Log.e("AuthVM", "Error saving auth data from OTP", e)
            }
        }
    }
}