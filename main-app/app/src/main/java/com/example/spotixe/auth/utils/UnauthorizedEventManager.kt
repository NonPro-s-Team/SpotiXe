package com.example.spotixe.auth.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.spotixe.auth.data.AuthDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manager to handle unauthorized (401) events across the app
 * When account is disabled, this will trigger dialog and logout
 */
object UnauthorizedEventManager {

    // Observable state for showing unauthorized dialog
    val showUnauthorizedDialog = mutableStateOf(false)

    // Message to display in dialog
    val dialogMessage = "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ hỗ trợ để biết thêm chi tiết."

    /**
     * Trigger unauthorized event
     * This will show dialog and prepare for logout
     */
    fun triggerUnauthorizedEvent(context: Context) {
        // Show dialog
        showUnauthorizedDialog.value = true
    }

    /**
     * Handle logout when user confirms dialog
     * Clear all session data and navigate to START
     */
    fun handleLogout(context: Context, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authDataStore = AuthDataStore(context)

                // Clear all user data
                authDataStore.clearAll()

                // Notify completion on main thread
                CoroutineScope(Dispatchers.Main).launch {
                    showUnauthorizedDialog.value = false
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    showUnauthorizedDialog.value = false
                    onComplete()
                }
            }
        }
    }

    /**
     * Reset the unauthorized state
     */
    fun reset() {
        showUnauthorizedDialog.value = false
    }
}
