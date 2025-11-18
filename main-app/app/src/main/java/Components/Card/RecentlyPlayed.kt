package Components.Card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotixe.Data.Song

@Composable
fun RecentlyPlayedItem(
    song: Song,
    modifier: Modifier = Modifier,
    onClickItem: (Song) -> Unit = {},
    onPlayClick: (Song) -> Unit = {}
) {
    Row(
        modifier = modifier
            .width(260.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF121212))
            .clickable { onClickItem(song) }
            .border(1.dp, Color(0x802E2E2E), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(song.coverRes),
            contentDescription = song.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(song.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
            Text(song.artist, fontSize = 15.sp, color = Color(0xFFBBBBBB), maxLines = 1)
        }
        FilledIconButton(
            onClick = { onPlayClick(song) },
            modifier = Modifier.size(50.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0xFF1DB954),
                contentColor = Color.Black
            )
        ) { Icon(Icons.Rounded.PlayArrow, contentDescription = "Play") }
    }
}
