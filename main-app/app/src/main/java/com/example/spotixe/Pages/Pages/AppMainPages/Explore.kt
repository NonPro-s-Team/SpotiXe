package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Buttons.BackButton
import Components.Card.ApiRecentlyPlayedItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spotixe.player.rememberPlayerVMActivity
import com.example.spotixe.viewmodel.SongViewModel

@Composable
fun ExploreScreen(navController: NavHostController) {
    val context = LocalContext.current
    val songViewModel = remember { SongViewModel(context) }
    val songs by songViewModel.songs.collectAsState()
    val isLoading by songViewModel.isLoading.collectAsState()
    
    val playerVM = rememberPlayerVMActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0),
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color(0xFF121212))
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                // ---------- HEADER ----------
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 8.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackButton(navController)

                        Text(
                            text = "Khám phá",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF58BA47)
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                }

                // ---------- SECTION 1: HIT MỚI HÔM NAY ----------
                item {
                    Text(
                        text = "Hit mới hôm nay",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                if (isLoading && songs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1DB954))
                        }
                    }
                } else {
                    // Grid layout for first 10 songs using chunked items
                    val hitSongs = songs.take(10)
                    val chunkedHitSongs = hitSongs.chunked(3)

                    items(chunkedHitSongs) { rowSongs ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSongs.forEach { song ->
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ApiRecentlyPlayedItem(
                                        song = song,
                                        onClickItem = {
                                            playerVM.playSong(song)
                                            navController.navigate("api_song_view/${song.songId}")
                                        }
                                    )
                                }
                            }
                            // Fill empty spaces in incomplete rows
                            repeat(3 - rowSongs.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(20.dp))
                }

                // ---------- SECTION 2: MỚI PHÁT HÀNH ----------
                item {
                    Text(
                        text = "Mới phát hành",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Grid layout for next 10 songs using chunked items
                val newReleaseSongs = songs.drop(10).take(10)
                val chunkedNewSongs = newReleaseSongs.chunked(3)

                items(chunkedNewSongs) { rowSongs ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSongs.forEach { song ->
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                ApiRecentlyPlayedItem(
                                    song = song,
                                    onClickItem = {
                                        playerVM.playSong(song)
                                        navController.navigate("api_song_view/${song.songId}")
                                    }
                                )
                            }
                        }
                        // Fill empty spaces in incomplete rows
                        repeat(3 - rowSongs.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
