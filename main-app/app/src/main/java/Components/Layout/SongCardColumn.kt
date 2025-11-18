package Components.Layout

import Components.Buttons.PlayButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.spotixe.Data.Song
import com.example.spotixe.MainRoute
import com.example.spotixe.player.PlayerViewModel
import com.google.android.play.integrity.internal.s


@Composable
fun SongCardColumn(
    song: Song,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val shape = RoundedCornerShape(12.dp)
    val playerVM: PlayerViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(shape)
            .background(Color(0xFF171717))
            .border(1.dp, Color(0x332E2E2E), shape)
            .clickable { navController.navigate(MainRoute.songView(song.id)) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(song.coverRes),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(9.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(
            modifier = Modifier.widthIn(max = 200.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(song.title, fontWeight = FontWeight.SemiBold, maxLines = 1, color = Color.White, fontSize = 20.sp)
            Text("${song.artist} • ${song.year}", fontSize = 13.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.weight(1f))
    }

}
