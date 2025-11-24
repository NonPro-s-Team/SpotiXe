package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Card.AlbumTile
import Components.Card.ArtistChip
import Components.Card.PlaylistCard
import Components.Card.ApiRecentlyPlayedItem
import Components.Card.RecentlyPlayedItem
import Components.Layout.ApiSongCardRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spotixe.Data.AlbumRepository
import com.example.spotixe.Data.ArtistRepository
import com.example.spotixe.Data.PlaylistRepository
import com.example.spotixe.MainRoute
import com.example.spotixe.auth.data.AuthDataStore
import com.example.spotixe.player.rememberPlayerVMActivity
import com.example.spotixe.viewmodel.SongViewModel

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val songViewModel = remember { SongViewModel(context) }
    val songs by songViewModel.songs.collectAsState()
    val isLoading by songViewModel.isLoading.collectAsState()
    
    val authDataStore = AuthDataStore(context)
    val userData by authDataStore.getUserData().collectAsState(initial = null)

    val playerVM = rememberPlayerVMActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0),
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color(0xFF121212))
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                // ---------- HEADER ----------
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Listen Now",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(40.dp)
                                .clickable {
                                    navController.navigate(MainRoute.User) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (userData?.avatarUrl != null && userData?.avatarUrl?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(userData?.avatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(55.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(55.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1DB954)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color(0xFF161616),
                        thickness = 1.dp
                    )
                }

                // ---------- TOP PICK ----------
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Top Pick",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "More from everyday",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                }

                item {
                    if (isLoading && songs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1DB954))
                        }
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            items(
                                items = songs.take(10),
                                key = { it.songId }
                            ) { song ->
                                ApiSongCardRow(
                                    song = song,
                                    navController = navController,
                                    playerViewModel = playerVM  // Truyền playerVM để tự động phát nhạc
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }

                // ---------- RECENTLY PLAYED ----------
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recently Played",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "See All",
                            color = Color(0xFF1DB954),
                            modifier = Modifier.clickable { /* navController.navigate("recently_all") */ }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        items(
                            items = songs.take(20),
                            key = { it.songId }
                        ) { song ->
                            ApiRecentlyPlayedItem(
                                song = song,
                                onClickItem = {
                                    // Click vào item → phát nhạc và mở full screen
                                    playerVM.playSong(song)
                                    navController.navigate("api_song_view/${song.songId}")
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }

                // ---------- PLAYLIST ----------
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Playlist",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "See All",
                            color = Color(0xFF1DB954),
                            modifier = Modifier.clickable { /* navController.navigate("playlist_all") */ }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = PlaylistRepository.all,
                            key = { it.id }
                        ) { pl ->
                            PlaylistCard(
                                playlist = pl,
                                onClick = {
                                    navController.navigate(MainRoute.playlistDetail(pl.id))
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }

                // ---------- ALBUMS ----------
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Albums",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "See All",
                            color = Color(0xFF1DB954),
                            modifier = Modifier.clickable { /* navController.navigate("albums_all") */ }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = AlbumRepository.all,
                            key = { it.id }
                        ) { album ->
                            AlbumTile(
                                album = album,
                                onClick = {
                                    navController.navigate(MainRoute.albumDetail(album.id))
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }

                // ---------- ARTISTS ----------
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Artists",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "See All",
                            color = Color(0xFF1DB954),
                            modifier = Modifier.clickable { /* navController.navigate("artists_all") */ }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = ArtistRepository.all,
                            key = { it.id }
                        ) { artist ->
                            ArtistChip(
                                name = artist.name,
                                coverRes = artist.coverRes,
                                onClick = {
                                    navController.navigate(MainRoute.artistDetail(artist.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
