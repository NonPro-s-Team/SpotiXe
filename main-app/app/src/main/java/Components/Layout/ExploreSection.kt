package Components.Layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import com.example.spotixe.Data.Song
import com.example.spotixe.Data.chunkByFour
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotixe.Data.chunkByFour

@Composable
fun ExploreSection(
    title: String,
    songs: List<Song>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val grouped = songs.chunkByFour()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { grouped.size }
    )

    Column(modifier.fillMaxWidth()) {

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(10.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            pageSpacing = 16.dp
        ) { page ->
            val group = grouped[page]

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                group.forEach { song ->
                    SongCardColumn(
                        song = song,
                        modifier = Modifier.fillMaxWidth(),
                        navController = navController
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { index ->
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(if (pagerState.currentPage == index) 10.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                Color.White
                            else Color.Gray.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}
