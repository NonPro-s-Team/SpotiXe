package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Buttons.BackButton
import Components.Layout.PlaylistSongRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spotixe.Data.Album
import com.example.spotixe.Data.Song
import com.example.spotixe.MainRoute
import com.example.spotixe.player.rememberPlayerVMActivity

@Composable
fun AlbumDetailScreen(
    navController: NavHostController,
    album: Album,
    songs: List<Song>
) {
    val playerVM = rememberPlayerVMActivity()
    Scaffold(
        containerColor = Color(0xFF121212),
        contentWindowInsets = WindowInsets(0),
    ) { inner ->
        Box(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {

            Column(modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
            ) {
                BackButton(navController)
                //Header lớn (gradient + tiêu đề + nút Play)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1B5E20), Color(0xFF121212))
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(album.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("${songs.size} songs", color = Color.White.copy(0.8f), fontSize = 13.sp)
                    }
                    // nút Play nổi
                    FilledIconButton(
                        onClick = { /* play all */ },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF1DB954),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = inner.calculateBottomPadding() + 96.dp)
                ) {
                    itemsIndexed(songs, key = { _, s -> s.id }) { index, s ->
                        PlaylistSongRow(
                            song = s,
                            onRowPlay = {                     // click cả dòng → play
                                playerVM.playFromOldList(songs, index)
                            },
                            onMoreClick = {                   // 3 chấm → vào SongView
                                navController.navigate(MainRoute.songView(s.id))
                                //mở bài hát
                            },
                        )
                    }
                }
            }
        }
    }
}
