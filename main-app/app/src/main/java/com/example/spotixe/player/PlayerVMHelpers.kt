package com.example.spotixe.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Helper function to remember PlayerViewModel in Activity scope
 */
@Composable
fun rememberPlayerVMActivity(): PlayerViewModel {
    val context = LocalContext.current
    return viewModel<PlayerViewModel>(
        factory = PlayerViewModelFactory(context.applicationContext as android.app.Application)
    )
}

