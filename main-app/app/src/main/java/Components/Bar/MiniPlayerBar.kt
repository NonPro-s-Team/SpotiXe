package Components.Bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.spotixe.player.PlayerViewModel
import com.example.spotixe.Data.model.Song

@Composable
fun MiniPlayerBar(
    playerViewModel: PlayerViewModel,
    onOpenSongView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSong by playerViewModel.currentSong.collectAsState(initial = null as Song?)
    val isPlaying by playerViewModel.isPlaying.collectAsState(initial = false)
    val progress by playerViewModel.progress.collectAsState(initial = 0f)

    val song = currentSong ?: return

    Column(
        modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E1E),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
    ) {
        // 🔹 Chỉ phần "nội dung" phía trên mới click mở full view
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bọc phần cover + text trong 1 Row riêng có clickable
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenSongView() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = song.coverImageUrl,
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = song.title,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artistName ?: "Artist",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(onClick = { playerViewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
        }

        ScrubbableProgressBar(
            progress = progress,          // 0f..1f
            height = 6.dp,                // tăng nhẹ để dễ vuốt hơn
            activeColor = Color(0xFF1DB954),
            inactiveColor = Color.White.copy(alpha = 0.4f),
            onSeekEnd = { p ->
                // Seek chỉ 1 lần khi thả tay
                playerViewModel.seekTo(p)
            },
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        )
    }
}
