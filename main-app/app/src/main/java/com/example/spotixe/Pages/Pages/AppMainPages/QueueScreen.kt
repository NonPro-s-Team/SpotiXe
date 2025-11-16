package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Bar.ScrubbableProgressBar
import Components.Buttons.BackButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spotixe.Data.Song
import com.example.spotixe.MainRoute
import com.example.spotixe.R
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.zIndex
import com.example.spotixe.player.rememberPlayerVMActivity

@Composable
fun QueueScreen(
    navController: NavHostController,
    current: Song,
    playingNext: List<Song>
) {
    val playerVM = rememberPlayerVMActivity()
    var isLiked by remember { mutableStateOf(false) }

    val isPlaying by playerVM.isPlaying.collectAsState()
    val progress by playerVM.progress.collectAsState()
    val currentPosition by playerVM.currentPosition.collectAsState()
    val duration by playerVM.duration.collectAsState()

    Scaffold(
        containerColor = Color(0xFF121212),
        contentWindowInsets = WindowInsets(0)
    ) { inner ->

        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            //Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                BackButton(navController)
                Spacer(Modifier.weight(1f))
            }

            //Current song
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(current.coverRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(Modifier.height(10.dp))
                Text(current.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(current.artist, color = Color.White.copy(0.7f), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { isLiked = !isLiked }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isLiked) "Liked" else "Not liked",
                            tint = Color(0xFF58BA47),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.list_clicked),
                        contentDescription = "playlist",
                        modifier = Modifier
                            .size(34.dp)
                            .offset(y = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF58BA47), shape = RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Playing Next", color = Color.White.copy(0.85f), fontSize = 13.sp)

            //Danh sách
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(playingNext) { s ->
                    NextRow(
                        song = s,
                        onClick = { navController.navigate(MainRoute.songView(s.id)) }
                    )
                }
            }

            //Footer: progress + controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 12.dp)
            ) {

                Spacer(Modifier.height(8.dp))

                ScrubbableProgressBar(
                    progress    = progress,
                    onSeek      = { p -> playerVM.seekTo(p) },
                    onSeekStart = { /* Not needed anymore */ },
                    onSeekEnd   = { /* Not needed anymore */ },
                    height      = 8.dp,
                    modifier    = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                )

                Row(Modifier.fillMaxWidth()) {
                    Text(formatTimeMs(currentPosition), color = Color.White.copy(0.7f), fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text(formatTimeMs(duration - currentPosition), color = Color.White.copy(0.7f), fontSize = 12.sp)
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { playerVM.playPrevious() }) {
                        Icon(Icons.Filled.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    FilledIconButton(
                        onClick = { playerVM.togglePlayPause() },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF1DB954),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    IconButton(onClick = { playerVM.playNext() }) {
                        Icon(Icons.Filled.SkipNext, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NextRow(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(song.coverRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontSize = 15.sp, maxLines = 1)
            Text(song.artist, color = Color.White.copy(0.7f), fontSize = 12.sp, maxLines = 1)
        }
        Icon(Icons.Filled.MoreVert, null, tint = Color.White.copy(0.7f))
    }
}

private fun formatTimeMs(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}
