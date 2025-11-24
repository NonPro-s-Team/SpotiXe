package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Buttons.BackButton
import Components.Card.SeeAllItemCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spotixe.Data.Album
import com.example.spotixe.Data.Artist
import com.example.spotixe.Data.Playlist
import com.example.spotixe.Data.model.Song
import com.example.spotixe.MainRoute
import com.example.spotixe.player.rememberPlayerVMActivity
import com.example.spotixe.viewmodel.SeeAllViewModel
import com.example.spotixe.Pages.Pages.AppMainPages.SeeAllType

/**
 * Universal "See All" Screen - tái sử dụng cho tất cả loại content
 *
 * @param navController: Điều hướng
 * @param type: Loại content (Recently Played, Playlists, Albums, Artists)
 * @param title: Tiêu đề hiển thị trên TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeAllScreen(
    navController: NavHostController,
    type: SeeAllType,
    title: String
) {
    val context = LocalContext.current
    val viewModel = remember { SeeAllViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()
    val playerVM = rememberPlayerVMActivity()

    // Load data khi screen được mount
    LaunchedEffect(type) {
        viewModel.loadData(type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    BackButton(navController)

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = Color(0xFF121212),
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(innerPadding)
        ) {
            when {
                // Loading state
                uiState.isLoading && uiState.items.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF1DB954)
                    )
                }

                // Error state
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Oops!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "Unknown error",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Empty state
                uiState.items.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No items found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Content - 2 column grid
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Chunk items into pairs for 2-column layout
                        items(uiState.items.chunked(2)) { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEachIndexed { index, item ->
                                    SeeAllItemCard(
                                        item = item,
                                        modifier = Modifier
                                            .weight(1f),
                                        onClick = {
                                            // Handle click theo loại item
                                            handleItemClick(
                                                item = item,
                                                type = type,
                                                navController = navController,
                                                playerVM = playerVM
                                            )
                                        }
                                    )
                                }

                                // Spacer cho item cuối nếu odd number
                                if (rowItems.size == 1) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Xử lý click event cho mỗi item type
 */
private fun handleItemClick(
    item: Any,
    type: SeeAllType,
    navController: NavHostController,
    playerVM: com.example.spotixe.player.PlayerViewModel
) {
    when (item) {
        is Song -> {
            // Click song → play nó và mở full screen
            playerVM.playSong(item)
            navController.navigate("api_song_view/${item.songId}")
        }
        is Playlist -> {
            // Click playlist → navigate tới playlist detail
            navController.navigate(MainRoute.playlistDetail(item.id))
        }
        is Album -> {
            // Click album → navigate tới album detail
            navController.navigate(MainRoute.albumDetail(item.id))
        }
        is Artist -> {
            // Click artist → navigate tới artist detail
            navController.navigate(MainRoute.artistDetail(item.id))
        }
    }
}

