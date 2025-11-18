package Components.Card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotixe.Data.Genre

fun LazyListScope.GenresSection(
    title: String,
    items: List<Genre>,
    onClickGenre: (Genre) -> Unit = {}
) {
    // header
    item(key = "$title-header") {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    //body
    val rows = items.chunked(2)
    items(rows.size, key = { "row-$it-$title" }) { rowIndex ->
        val row = rows[rowIndex]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenreCard(genre = row[0], modifier = Modifier.weight(1f), onClick = { onClickGenre })
            if (row.size > 1) {
                GenreCard(
                    genre = row[1],
                    modifier = Modifier.weight(1f),
                    onClick = { onClickGenre })
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}


