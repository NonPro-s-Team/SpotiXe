package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Buttons.BackButton
import Components.Layout.OptionRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.spotixe.Data.Song

@Composable
fun SongMoreScreen(
    navController: NavHostController,
    song: Song,
    onLike: () -> Unit = {},
    onViewArtist: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onAddToQueue: () -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = Color(0xFF121212),
        contentWindowInsets = WindowInsets(0)
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color(0xFF121212))
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(navController)
            }

            // Cover
            Image(
                painter = painterResource(id = song.coverRes),
                contentDescription = song.title,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            // Title + artist
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = song.artist,
                color = Color.White.copy(0.7f),
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // Options
            OptionRow(
                onClick = {  isLiked = !isLiked  },
                icon = {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isLiked) "Liked" else "Not liked",
                        tint = Color(0xFF58BA47),
                        modifier = Modifier.size(28.dp)
                    )
                },
                text = "Like",
                rowHeight = 64.dp,
                textSize = 18.sp
            )

            OptionRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color(0xFF58BA47),
                        modifier = Modifier.size(28.dp)
                    )
                },
                text = "View Artist",
                onClick = onLike,
                rowHeight = 64.dp,
                textSize = 18.sp
            )

            OptionRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.LibraryMusic,
                        contentDescription = null,
                        tint = Color(0xFF58BA47),
                        modifier = Modifier.size(28.dp)
                    )
                },
                text = "Add to playlist",
                onClick = onLike,
                rowHeight = 64.dp,
                textSize = 18.sp
            )

            OptionRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.QueueMusic,
                        contentDescription = null,
                        tint = Color(0xFF58BA47),
                        modifier = Modifier.size(28.dp)
                    )
                },
                text = "Add to queue",
                onClick = onLike,
                rowHeight = 64.dp,
                textSize = 18.sp
            )


            Spacer(Modifier.height(24.dp))
        }
    }
}



