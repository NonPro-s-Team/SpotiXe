package Components.Card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotixe.Data.Genre

@Composable
fun GenreCard(
    genre: Genre,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(2f)
            .clip(RoundedCornerShape(18.dp))
            .background(genre.color)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(
            text = genre.title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Image(
            painter = painterResource(genre.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.BottomEnd)
                .graphicsLayer(
                    rotationZ = -15f,
                    translationX = 10f,
                    translationY = 6f
                ),
            contentScale = ContentScale.Crop
        )
    }
}