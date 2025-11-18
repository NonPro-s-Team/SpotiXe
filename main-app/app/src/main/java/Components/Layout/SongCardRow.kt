package Components.Layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import com.example.spotixe.Data.Song
import androidx.navigation.NavController
import com.example.spotixe.MainRoute


@Composable
fun SongCardRow(
    song: Song,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(
        modifier = modifier
            .width(260.dp)
            .height(330.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1A1A))
            .border((1.dp), Color(0x332E2E2E), RoundedCornerShape(20.dp))
            .clickable {
                navController.navigate(MainRoute.songView(song.id))
            }
    ) {
        // Cover
        Image(
            painter = painterResource(song.coverRes),
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
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        )

        // Text info dưới (title / artist / year)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = song.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = song.artist,
                fontSize = 13.sp,
                color = Color(0xFFDDDDDD)
            )
            Text(
                text = song.year,
                fontSize = 12.sp,
                color = Color(0xFFBDBDBD)
            )
        }
    }
}
