package Components.Layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.spotixe.Data.Song

@Composable
fun PlaylistSongRow(
    song: Song,
    modifier: Modifier = Modifier,
    onRowPlay: () -> Unit,                 // ⬅️ click vào ROW thì phát (mini player)
    onMoreClick: () -> Unit,               // ⬅️ click 3 chấm thì navigate SongView
    trailing: (@Composable () -> Unit)? = null,
    showDivider: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable { onRowPlay() }      // ⬅️ phát bài
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(song.coverRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontSize = 16.sp, maxLines = 1)
            Text("${song.artist} · ${song.year}", color = Color.White.copy(0.7f), fontSize = 12.sp, maxLines = 1)
        }

        // 3 chấm → chỉ navigate, không phát
        IconButton(onClick = onMoreClick, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Color.White.copy(0.7f))
        }

        trailing?.let {
            Spacer(Modifier.width(6.dp))
            it()
        }
    }
    if (showDivider) Divider(color = Color.White.copy(0.08f))
}
