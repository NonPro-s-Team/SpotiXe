package Components.Card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spotixe.Data.model.Song

@Composable
fun ApiRecentlyPlayedItem(
    song: Song,
    modifier: Modifier = Modifier,
    onClickItem: (Song) -> Unit = {}
) {
    val context = LocalContext.current
    
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
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ){
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(song.coverImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = song.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(75.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = song.getFormattedDuration(),
                    fontSize = 15.sp,
                    color = Color(0xFFBBBBBB),
                    maxLines = 1
                )
            }
        }
    }
}
