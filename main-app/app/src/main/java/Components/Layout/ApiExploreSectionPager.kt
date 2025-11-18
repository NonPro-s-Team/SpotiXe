package Components.Layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spotixe.Data.model.Song
import com.example.spotixe.player.PlayerViewModel

/**
 * ExploreSection with HorizontalPager for API songs
 * Based on the original ExploreSection design with pager and dots indicator
 */
@Composable
fun ApiExploreSectionPager(
    title: String,
    songs: List<Song>,
    navController: NavHostController,
    playerVM: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    // Group songs by 4 (similar to original chunkByFour)
    val grouped = songs.chunked(4)
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { grouped.size }
    )

    Column(modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(10.dp))

        // HorizontalPager with song items
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            pageSpacing = 16.dp
        ) { page ->
            val group = grouped[page]

            // Layout: 2 rows x 2 columns = 4 songs per page
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Split into rows of 2 songs each
                group.chunked(2).forEach { rowSongs ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowSongs.forEach { song ->
                            Box(modifier = Modifier.weight(1f)) {
                                ApiSongCardRow(
                                    song = song,
                                    navController = navController,
                                    playerViewModel = playerVM,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        // Fill empty space if row has only 1 song
                        if (rowSongs.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // Pager indicator dots
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
