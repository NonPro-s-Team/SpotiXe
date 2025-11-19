package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Buttons.BackButton
import Components.Card.ApiRecentlyPlayedItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
fun SearchScreen(
    navController: NavHostController,
) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {

        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val fontScale = screenWidth.value / 400f   // scale text theo độ rộng
        val dynamicPadding = screenHeight * 0.02f  // padding theo màn hình

        val context = LocalContext.current
        val songViewModel = remember { SongViewModel(context) }
        val songs by songViewModel.songs.collectAsState()
        val isLoading by songViewModel.isLoading.collectAsState()

        var text by rememberSaveable { mutableStateOf("") }
        val playerVM = rememberPlayerVMActivity()

        val filteredSongs = remember(songs, text) {
            if (text.isBlank()) songs
            else songs.filter { it.title.contains(text, ignoreCase = true) }
        }

        // Auto-grid responsive
        val gridCount = (screenWidth / 140.dp).toInt().coerceIn(2, 4)

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0)
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = dynamicPadding)
            ) {

                // ───── HEADER ─────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dynamicPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Tìm kiếm",
                        fontSize = (28 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF58BA47)
                    )
                }

                Spacer(Modifier.height(dynamicPadding))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(dynamicPadding))

                // ───── SEARCH BAR ─────
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Thông tin nhập",
                            color = Color(0xFF9E9E9E),
                            fontSize = (14 * fontScale).sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFB0BEC5),
                        unfocusedIndicatorColor = Color(0xFFB0BEC5),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(dynamicPadding))

                // ───── LIST TITLE ─────
                Text(
                    text = if (text.isNotBlank())
                        "Kết quả tìm kiếm (${filteredSongs.size})"
                    else "Tất cả bài hát",
                    fontSize = (18 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = dynamicPadding)
                )

                // ───── LOADING STATE ─────
                if (isLoading && songs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1DB954))
                    }
                    return@Scaffold
                }

                // ───── EMPTY STATE ─────
                if (filteredSongs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Không tìm thấy kết quả",
                            color = Color.Gray,
                            fontSize = (16 * fontScale).sp
                        )
                    }
                    return@Scaffold
                }

                // ───── GRID ─────
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridCount),
                    contentPadding = PaddingValues(
                        bottom = 110.dp,
                        top = 6.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredSongs,
                        key = { it.songId }
                    ) { song ->
                        ApiRecentlyPlayedItem(
                            song = song,
                            onClickItem = {
                                playerVM.playSong(song)
                                navController.navigate("api_song_view/${song.songId}")
                            }
                        )
                    }
                }
            }
        }
    }
}
