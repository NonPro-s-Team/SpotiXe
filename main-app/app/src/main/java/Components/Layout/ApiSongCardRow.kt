package Components.Layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spotixe.Data.model.Song
import com.example.spotixe.player.PlayerViewModel

@Composable
fun ApiSongCardRow(
    song: Song,
    modifier: Modifier = Modifier,
    navController: NavController,
    playerViewModel: PlayerViewModel? = null
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .width(260.dp)
            .height(330.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1A1A))
            .border((1.dp), Color(0x332E2E2E), RoundedCornerShape(20.dp))
            .clickable {
                // Click vào card → phát nhạc và mở full screen
                playerViewModel?.playSong(song)
                navController.navigate("api_song_view/${song.songId}")
            }
    ) {
        // Cover from API URL
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(song.coverImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = song.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = song.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = song.getFormattedDuration(),
                    fontSize = 13.sp,
                    color = Color(0xFFAAAAAA)
                )
            }
        }
    }
}
