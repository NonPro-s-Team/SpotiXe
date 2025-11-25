package com.example.spotixe.auth.data.repository

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotixe.auth.viewmodel.AuthViewModel

class AuthViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
