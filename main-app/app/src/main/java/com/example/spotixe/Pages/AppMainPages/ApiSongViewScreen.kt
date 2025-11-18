package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Bar.ScrubbableProgressBar
import Components.Buttons.BackButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.spotixe.R
import com.example.spotixe.Data.model.Song
import com.example.spotixe.player.PlayerViewModel
import com.example.spotixe.viewmodel.SongViewModel

@Composable
fun ApiSongViewScreen(
    navController: NavHostController,
    songId: Long,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val songViewModel = remember { SongViewModel(context) }
    
    var song by remember { mutableStateOf<Song?>(null) }
    var artistName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Load song by ID
    LaunchedEffect(songId) {
        songViewModel.getSongById(songId) { result ->
            if (result != null) {
                song = result
                isLoading = false

                // Chỉ phát nhạc nếu đây là bài hát mới (khác với bài đang phát)
                // Nếu đang phát cùng bài thì không reset về giây 0
                val currentlyPlayingSong = playerViewModel.currentSong.value
                if (currentlyPlayingSong == null || currentlyPlayingSong.songId != result.songId) {
                    playerViewModel.playSong(result)
                }
                // Nếu đã đang phát bài này rồi thì giữ nguyên vị trí hiện tại

                // Lấy tên nghệ sĩ nếu có artistId
                result.artistId?.let { artistId ->
                    songViewModel.getArtistById(artistId) { artist ->
                        artistName = artist?.name
                    }
                }
            } else {
                error = "Song not found"
                isLoading = false
            }
        }
    }
    
    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    val duration by playerViewModel.duration.collectAsState()
    val repeatMode by playerViewModel.repeatMode.collectAsState()
    val playlist by playerViewModel.playlist.collectAsState()
    val currentIndex by playerViewModel.currentIndex.collectAsState()

    var isLiked by remember { mutableStateOf(false) }

    // Check if there are previous/next songs in the playlist
    val hasPrevious = playlist.size > 1 && currentIndex > 0
    val hasNext = playlist.size > 1 && currentIndex < playlist.size - 1

    Scaffold(
        containerColor = Color(0xFF121212),
        contentWindowInsets = WindowInsets(0)
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $error",
                            color = Color.White
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
                song != null -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp)
                        ) {
                            BackButton(navController)
                            Spacer(Modifier.weight(1f))
                        }

                        // Cover image from API
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = song!!.coverImageUrl,
                                contentDescription = song!!.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            
                            // Gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            )
                                        )
                                    )
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // Title và Artist Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    song!!.title,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                // Hiển thị tên nghệ sĩ thay vì genre
                                Text(
                                    artistName ?: "Loading artist...",
                                    color = Color.White.copy(0.7f),
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Progress slider
                        ScrubbableProgressBar(
                            progress    = progress,
                            onSeek      = { p -> playerViewModel.seekTo(p) },
                            onSeekStart = { },
                            onSeekEnd   = { },
                            height      = 8.dp,
                            modifier    = Modifier
                                .fillMaxWidth()
                                .zIndex(1f)
                        )

                        // Time labels
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                formatTimeMs(currentPosition),
                                color = Color.White.copy(0.7f),
                                fontSize = 12.sp
                            )
                            Text(
                                "-${formatTimeMs(maxOf(0, duration - currentPosition))}",
                                color = Color.White.copy(0.7f),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        // Playback controls
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Like button (bên trái nhất)
                            IconButton(onClick = { isLiked = !isLiked }) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (isLiked) "Liked" else "Not liked",
                                    tint = Color(0xFF58BA47),
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            // Previous button (bên trái nút play)
                            IconButton(
                                onClick = { playerViewModel.playPrevious() },
                                enabled = hasPrevious // Disable if no previous song
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            // Play/Pause button (ở giữa)
                            FilledTonalButton(
                                onClick = {
                                    if (currentSong?.songId == song!!.songId) {
                                        playerViewModel.togglePlayPause()
                                    } else {
                                        playerViewModel.playSong(song!!)
                                    }
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF58BA47)
                                ),
                                modifier = Modifier.size(72.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying && currentSong?.songId == song!!.songId)
                                        Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            // Next button (bên phải nút play)
                            IconButton(
                                onClick = { playerViewModel.playNext() },
                                enabled = hasNext // Disable if no next song
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            // Repeat button (bên phải nhất)
                            IconButton(
                                onClick = {
                                    // Cycle through repeat modes: 0 -> 1 -> 2 -> 0
                                    val newMode = when (repeatMode) {
                                        0 -> 1  // Off -> Repeat Once
                                        1 -> 2  // Repeat Once -> Repeat All
                                        else -> 0  // Repeat All -> Off
                                    }
                                    playerViewModel.setRepeatMode(newMode)
                                }
                            ) {
                                Icon(
                                    imageVector = when (repeatMode) {
                                        1 -> Icons.Filled.RepeatOne  // Repeat once
                                        2 -> Icons.Filled.Repeat     // Repeat all (highlighted)
                                        else -> Icons.Filled.Repeat  // Off (dimmed)
                                    },
                                    contentDescription = when (repeatMode) {
                                        0 -> "Repeat Off"
                                        1 -> "Repeat Once"
                                        else -> "Repeat All"
                                    },
                                    tint = if (repeatMode > 0) Color(0xFF58BA47) else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

private fun formatTimeMs(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}
