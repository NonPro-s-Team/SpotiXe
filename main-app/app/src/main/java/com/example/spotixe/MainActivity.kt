package com.example.spotixe

import Components.Bar.MiniPlayerBar
import Components.Bar.BottomBar
import Components.SetSystemBars
import Components.Layout.SpotixeDialog
import com.example.spotixe.auth.utils.UnauthorizedEventManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.spotixe.Data.AlbumRepository
import com.example.spotixe.Data.ArtistRepository
import com.example.spotixe.Data.PlaylistRepository
import com.example.spotixe.Data.SongRepository
import com.example.spotixe.Pages.Pages.AppMainPages.*
import com.example.spotixe.Pages.Pages.SignInPages.Sign_in1Screen
import com.example.spotixe.Pages.Pages.SignInPages.Sign_in2Screen
import com.example.spotixe.Pages.Pages.SignUpPages.Sign_UpEmail1Screen
import com.example.spotixe.Pages.Pages.SignUpPages.Sign_UpEmail2Screen
import com.example.spotixe.Pages.Pages.SignUpPages.Sign_UpPhone1Screen
import com.example.spotixe.Pages.Pages.SignUpPages.Sign_UpPhone2Screen
import com.example.spotixe.Pages.Pages.SignUpPages.Sign_UpPhone3Screen
import com.example.spotixe.Pages.Pages.StartPages.Start2Screen
import com.example.spotixe.Pages.Pages.StartPages.StartScreen
import com.example.spotixe.player.PlayerViewModel
import com.example.spotixe.player.MusicPlayerService
import com.example.spotixe.ui.theme.SpotiXeTheme
import androidx.lifecycle.ViewModelProvider
import com.example.spotixe.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.firstOrNull

class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null
    private var playerVM: PlayerViewModel? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//
//            val token = task.result
//            Log.d("FCM_TOKEN", "Token: $token")
//
//            // Nếu muốn thì show toast:
//             Toast.makeText(this, "Token: $token", Toast.LENGTH_SHORT).show()
//        }


        setContent {
            navController = rememberNavController()
            // VM phát nhạc dùng chung toàn app (scope Activity) - NEW ExoPlayer-based
            playerVM = remember {
                ViewModelProvider(
                    this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )[PlayerViewModel::class.java]
            }
            // Authentication ViewModel
            val authVM: AuthViewModel = viewModel()

            // Observe unauthorized dialog state
            val showUnauthorizedDialog by UnauthorizedEventManager.showUnauthorizedDialog

            // Handle intent to navigate to player screen when notification is clicked
            LaunchedEffect(intent) {
                playerVM?.let { vm ->
                    navController?.let { nav ->
                        handleIntentNavigation(intent, nav, vm)
                    }
                }
            }

            // Unauthorized Dialog - shown when account is disabled (401)
            SpotixeDialog(
                visible = showUnauthorizedDialog,
                title = "Tài khoản bị vô hiệu hóa",
                message = UnauthorizedEventManager.dialogMessage,
                primaryButtonText = "OK",
                onPrimaryClick = {
                    // Handle logout and navigate to START
                    UnauthorizedEventManager.handleLogout(this@MainActivity) {
                        navController?.navigate(Graph.START) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onDismissRequest = {
                    // Same action as primary button
                    UnauthorizedEventManager.handleLogout(this@MainActivity) {
                        navController?.navigate(Graph.START) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )

            SpotiXeTheme {
                SetSystemBars()

                // Theo dõi route hiện tại để quyết định show/hide các thanh
                val backStack by navController!!.currentBackStackEntryAsState()
                val dest = backStack?.destination

                // Những route FULL-SCREEN muốn ẩn BottomBar
                val hideBottomOnRoutes = setOf(
                    MainRoute.SongView,
                    MainRoute.SongViewMore,
                    MainRoute.UserDetail,
                    MainRoute.SeeAll
                )

                // Check if current route matches api_song_view pattern
                val currentRoute = dest?.route
                val isApiSongView = currentRoute?.startsWith("api_song_view/") == true

                val showBottomBar =
                    dest.isInGraph(Graph.MAIN) &&
                            dest?.route !in hideBottomOnRoutes &&
                            !isApiSongView  // Ẩn bottom bar khi đang ở màn hình api song view

                // (Tuỳ chọn) Ẩn MiniPlayerBar ở một vài màn full-screen
                val hideMiniOnRoutes = setOf(
                    MainRoute.SongView,
                    MainRoute.Playlist,
                    "api_song_view/{songId}"  // Ẩn mini player khi đang ở full screen player
                )

                val showMini =
                    dest.isInGraph(Graph.MAIN) &&
                            dest?.route !in hideMiniOnRoutes &&
                            !isApiSongView  // Ẩn nếu đang ở màn hình full screen player

                Scaffold(
                    containerColor = Color(0xFF121212),
                    bottomBar = { if (showBottomBar) BottomBar(navController!!) }
                ) { inner ->
                    Box(Modifier.fillMaxSize()) {
                        // ----- NAV HOST -----
                        // Determine start destination based on login status
                        val isLoggedIn by authVM.isLoggedIn.collectAsState()
                        val startDest = if (isLoggedIn) Graph.MAIN else Graph.START
//                        val startDest = Graph.MAIN  // TEMP: BỎ QUA START/ AUTH ĐỂ DỄ TEST

                        NavHost(
                            navController = navController!!,
                            startDestination = startDest,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(inner)
                        ) {
                            // START GRAPH
                            navigation(
                                startDestination = StartRoute.Start1,
                                route = Graph.START
                            ) {
                                composable(StartRoute.Start1) { StartScreen(navController!!) }
                                composable(StartRoute.Start2) { Start2Screen(navController!!) }
//                                composable(StartRoute.Start3) { Start3Screen(navController!!) }
                            }

                            // AUTH GRAPH
                            navigation(
                                startDestination = AuthRoute.SignUpPhone1,
                                route = Graph.AUTH
                            ) {
                                composable(AuthRoute.SignIn1) { Sign_in1Screen(navController!!) }
                                composable(AuthRoute.SignIn2) { Sign_in2Screen(navController!!) }
                                composable(AuthRoute.SignUpEmail1) { Sign_UpEmail1Screen(navController!!) }
                                composable(AuthRoute.SignUpEmail2) { Sign_UpEmail2Screen(navController!!) }
                                composable(AuthRoute.SignUpPhone1) { Sign_UpPhone1Screen(navController!!) }
                                composable(AuthRoute.SignUpPhone2) { Sign_UpPhone2Screen(navController!!) }
                                composable(AuthRoute.SignUpPhone3) { Sign_UpPhone3Screen(navController!!) }
                            }

                            // MAIN GRAPH
                            navigation(
                                startDestination = MainRoute.Home,
                                route = Graph.MAIN
                            ) {
                                composable(MainRoute.Home) { HomeScreen(navController!!) }
                                composable(MainRoute.Explore) { ExploreScreen(navController!!) }
                                composable(MainRoute.Search) { SearchScreen(navController!!) }
                                composable(MainRoute.User) { UserScreen(navController!!) }
                                composable(MainRoute.UserDetail) { UserDetailScreen(navController!!) }
                                composable(MainRoute.ErrorScreen) {
                                    ErrorScreen(
                                        message = "An unexpected error occurred.",
                                        onRetry = { /* TODO: implement retry logic */ }
                                    )
                                }

                                // SongView (old - local data)
                                composable(
                                    route = MainRoute.SongView,
                                    arguments = listOf(navArgument("songId")  { type = NavType.StringType })
                                ) { be ->
                                    val songId = be.arguments?.getString("songId") ?: return@composable
                                    val song = remember(songId) { SongRepository.get(songId) }
                                    if (song != null) {
                                        SongViewScreen(navController!!, song)
                                    } else {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text("Song not found", color = Color.White)
                                        }
                                    }
                                }

                                // API Song View (new - with ExoPlayer)
                                composable(
                                    route = "api_song_view/{songId}",
                                    arguments = listOf(navArgument("songId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val songId = backStackEntry.arguments?.getLong("songId") ?: return@composable
                                    ApiSongViewScreen(
                                        navController = navController!!,
                                        songId = songId,
                                        playerViewModel = playerVM!!
                                    )
                                }

                                // SongViewMore (menu more)
                                composable(
                                    route = MainRoute.SongViewMore,
                                    arguments = listOf(navArgument("songId") { type = NavType.StringType })
                                ) { be ->
                                    val songId = be.arguments?.getString("songId") ?: return@composable
                                    val song = SongRepository.get(songId)
                                    if (song != null) {
                                        SongMoreScreen(navController!!, song)
                                    }
                                }

                                // Queue / Playlist (đã phát)
                                composable(
                                    route = MainRoute.Playlist,
                                    arguments = listOf(navArgument("songId") { type = NavType.StringType })
                                ) { be ->
                                    val songId = be.arguments?.getString("songId") ?: return@composable
                                    val current = SongRepository.get(songId)
                                    val nextList = remember(songId) { SongRepository.all.filter { it.id != songId } }
                                    if (current != null) {
                                        QueueScreen(
                                            navController = navController!!,
                                            current = current,
                                            playingNext = nextList
                                        )
                                    } else {
                                        QueueScreen(
                                            navController = navController!!,
                                            current = SongRepository.all.first(),
                                            playingNext = SongRepository.all.drop(1)
                                        )
                                    }
                                }

                                // Playlist detail
                                composable(
                                    route = MainRoute.PlaylistDetail,
                                    arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                                ) { be ->
                                    val pid = be.arguments?.getString("playlistId") ?: return@composable
                                    val pl = remember(pid) { PlaylistRepository.get(pid) }
                                    val songs = remember(pl) { pl?.let { PlaylistRepository.songsOf(it) } ?: emptyList() }
                                    if (pl != null) {
                                        PlaylistDetailScreen(navController!!, pl, songs)
                                    } else {
                                        Text("Playlist not found", color = Color.White)
                                    }
                                }

                                // Album detail
                                composable(
                                    route = MainRoute.AlbumDetail,
                                    arguments = listOf(navArgument("albumId") { type = NavType.StringType })
                                ) { be ->
                                    val albumId = be.arguments?.getString("albumId") ?: return@composable
                                    val album = AlbumRepository.get(albumId)
                                    val songs = album?.let { AlbumRepository.songsOf(it) } ?: emptyList()
                                    if (album != null) {
                                        AlbumDetailScreen(navController!!, album, songs)
                                    } else {
                                        Text("Album not found", color = Color.White)
                                    }
                                }

                                // See All Screen
                                composable(
                                    route = MainRoute.SeeAll,
                                    arguments = listOf(navArgument("type") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val typeArg = backStackEntry.arguments?.getString("type") ?: "recently_played"
                                    val seeAllType = SeeAllType.from(typeArg)
                                    val title = SeeAllType.toTitle(seeAllType)

                                    SeeAllScreen(
                                        navController = navController!!,
                                        type = seeAllType,
                                        title = title
                                    )
                                }

                                // Artist detail
                                composable(
                                    route = MainRoute.ArtistDetail,
                                    arguments = listOf(navArgument("artistId") { type = NavType.StringType })
                                ) { be ->
                                    val id = be.arguments?.getString("artistId") ?: return@composable
                                    val artist = ArtistRepository.get(id)
                                    if (artist != null) {
                                        val album = artist.albumId?.let { AlbumRepository.get(it) }
                                        val topSongs = artist.topSongIds.mapNotNull { SongRepository.get(it) }
                                        ArtistDetailScreen(
                                            navController = navController!!,
                                            artistName = artist.name,
                                            coverRes = artist.coverRes,
                                            album = album,
                                            albumReleaseDate = artist.albumReleaseDate,
                                            topSongs = topSongs
                                        )
                                    } else {
                                        Text("Artist not found", color = Color.White)
                                    }
                                }
                            }
                        }
                        // ----- /NAV HOST -----

                        // MiniPlayerBar overlay (ngoài NavHost, nằm trên BottomBar)
                        if (showMini) {
                            MiniPlayerBar(
                                playerViewModel = playerVM!!,
                                onOpenSongView = {
                                    playerVM!!.currentSong.value?.songId?.let { songId ->
                                        navController!!.navigate("api_song_view/$songId")
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = inner.calculateBottomPadding() + 8.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Handle notification click when app is already running
        if (intent.action == MusicPlayerService.ACTION_OPEN_PLAYER) {
            playerVM?.currentSong?.value?.let { song ->
                navController?.navigate("api_song_view/${song.songId}") {
                    launchSingleTop = true
                }
            }
        }
    }
}

// Helper: kiểm tra destination có thuộc 1 graph không
private fun NavDestination?.isInGraph(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true

private suspend fun handleIntentNavigation(
    intent: android.content.Intent,
    navController: NavHostController,
    playerVM: PlayerViewModel
) {
    if (intent.action == MusicPlayerService.ACTION_OPEN_PLAYER) {
        // Get current song without blocking indefinitely
        playerVM.currentSong.firstOrNull { it != null }?.let { song ->
            navController.navigate("api_song_view/${song.songId}") {
                launchSingleTop = true
            }
        }
    }
}
