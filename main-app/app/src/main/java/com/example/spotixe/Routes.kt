package com.example.spotixe
object Graph {
    const val START = "graph_start"
    const val AUTH  = "graph_auth"
    const val MAIN  = "graph_main"
}

object StartRoute {
    const val Start1 = "start1"
    const val Start2 = "start2"
}

object AuthRoute {
    const val SignIn1 = "sign_in1"
    const val SignUpEmail1 = "sign_upEmail1"
    const val SignUpEmail2 = "sign_upEmail2"
    const val SignUpPhone1 = "sign_upPhone1"
    const val SignUpPhone2 = "sign_upPhone2"
    const val SignUpPhone3 = "sign_upPhone3"
}

object MainRoute {
    const val Home    = "home"
    const val Explore = "explore"
    const val Search  = "search"
    const val User    = "user"
    const val UserDetail = "user_detail"

    // Song View (có songId)
    const val SongView = "main/song/view/{songId}"
    fun songView(songId: String) = "main/song/view/$songId"
    // Song View More (có songId)
    const val SongViewMore = "main/song/view_more/{songId}"
    fun songViewMore(songId: String) = "main/song/view_more/$songId"
    // Song View Queue (có songId)
    const val Playlist  = "main/playlist/{songId}"
    fun playlist(songId: String) = "main/playlist/$songId"
    // Playlistscreen (có playlistId)
    const val PlaylistDetail = "main/playlist/detail/{playlistId}"
    fun playlistDetail(playlistId: String) = "main/playlist/detail/$playlistId"
    //AlbumScreen (có albumId)
    const val AlbumDetail = "main/album/{albumId}"
    fun albumDetail(albumId: String) = "main/album/$albumId"

    // ArtistScreen (có artistId)
    const val ArtistDetail = "main/artist/{artistId}"
    fun artistDetail(id: String) = "main/artist/$id"

    const val ErrorScreen="error_screen"

    const val NotificationPermissionScreen = "test_screen"

}

