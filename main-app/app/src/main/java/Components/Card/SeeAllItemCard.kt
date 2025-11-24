package Components.Card

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spotixe.Data.Album
import com.example.spotixe.Data.Artist
import com.example.spotixe.Data.Playlist
import com.example.spotixe.Data.model.Song

/**
 * Unified card component cho tất cả loại items trong See All screens
 * Hỗ trợ Song, Playlist, Album, Artist
 * Layout: 2 cột, aspect ratio 16:9 (landscape)
 */
@Composable
fun SeeAllItemCard(
    item: Any,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .clip(shape)
            .background(Color(0xFF171717))
            .border(1.dp, Color(0x332E2E2E), shape)
            .clickable { onClick() }
    ) {
        // Render cover image dựa trên loại item
        when (item) {
            is Song -> {
                // API Song - dùng URL
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.coverImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    contentScale = ContentScale.Crop
                )
            }
            is Playlist -> {
                // Local Playlist - dùng drawable resource
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(item.coverRes),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    contentScale = ContentScale.Crop
                )
            }
            is Album -> {
                // Local Album - dùng drawable resource
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(item.coverRes),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    contentScale = ContentScale.Crop
                )
            }
            is Artist -> {
                // Local Artist - dùng drawable resource
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(item.coverRes),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        )

        // Text info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            when (item) {
                is Song -> {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.artistName ?: "Unknown",
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                is Playlist -> {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Playlist",
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD)
                    )
                }
                is Album -> {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.artist,
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                is Artist -> {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Artist",
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD)
                    )
                }
            }
        }
    }
}

