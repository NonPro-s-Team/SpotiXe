package com.example.spotixe.Pages.Pages.AppMainPages


import Components.Bar.MiniPlayerBar
import Components.Bar.BottomBar
import Components.Buttons.BackButton
import Components.Layout.PlaylistSongRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.spotixe.Data.Album
import com.example.spotixe.Data.Song
import com.example.spotixe.Graph
import com.example.spotixe.MainRoute
import com.example.spotixe.R
import com.example.spotixe.player.PlayerViewModel
import com.example.spotixe.player.rememberPlayerVMActivity


@Composable
fun ArtistDetailScreen(
    navController: NavHostController,
    artistName: String,
    coverRes: Int,
    album: Album?,
    albumReleaseDate: String?,
    topSongs: List<Song>
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

                //Header: cover lớn + tên nghệ sĩ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .statusBarsPadding()
                ) {
                    Image(
                        painter = painterResource(coverRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(96.dp)
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    1f to Color(0xCC000000)
                                )
                            )
                    )
                    // artist name
                    Text(
                        text = artistName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                }

                //Album section
                if (album != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickable { navController.navigate(MainRoute.albumDetail(album.id)) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(album.coverRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(album.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(artistName, color = Color.White.copy(0.8f), fontSize = 13.sp, maxLines = 1)
                            if (!albumReleaseDate.isNullOrBlank()) {
                                Text(albumReleaseDate, color = Color.White.copy(0.6f), fontSize = 12.sp)
                            }
                            Text("${album.songIds.size} Songs", color = Color.White.copy(0.6f), fontSize = 12.sp)
                        }
                        FilledIconButton(
                            onClick = { /* play album */ },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFF1DB954),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, null)
                        }
                    }
                }

                //Top Songs
                Text(
                    "Top Songs",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = inner.calculateBottomPadding() + 96.dp)
                ) {
                    itemsIndexed(topSongs, key = { _, s -> s.id }) { index, s ->
                        PlaylistSongRow(
                            song = s,
                            onRowPlay = {                     // click cả dòng → play
                                playerVM.playFromOldList(topSongs, index)
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
