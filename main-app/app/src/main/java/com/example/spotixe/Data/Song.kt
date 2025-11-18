package com.example.spotixe.Data

import com.example.spotixe.R

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val year: String,
    val coverRes: Int // R.drawable.*
)

val topPicks = listOf(
    Song("1","Escape of the Phoenix","Evergrey","2021", R.drawable.spotixe_logo),
    Song("2","A Hunter’s Tale","Evergrey","2020", R.drawable.spotixe_logo),
    Song("3","Powerslave","Iron Maiden","1984", R.drawable.spotixe_logo),
    Song("4","Escape of the Phoenix","Evergrey","2021", R.drawable.spotixe_logo),
    Song("5","A Hunter’s Tale","Evergrey","2020", R.drawable.spotixe_logo),
    Song("6","Powerslave","Iron Maiden","1984", R.drawable.spotixe_logo),
    Song("7","Escape of the Phoenix","Evergrey","2021", R.drawable.spotixe_logo),
    Song("8","A Hunter’s Tale","Evergrey","2020", R.drawable.spotixe_logo),
    Song("9","Powerslave","Iron Maiden","1984", R.drawable.spotixe_logo),
    Song("10","Escape of the Phoenix","Evergrey","2021", R.drawable.spotixe_logo),
    Song("11","A Hunter’s Tale","Evergrey","2020", R.drawable.spotixe_logo),
    Song("12","Powerslave","Iron Maiden","1984", R.drawable.spotixe_logo),
)

val recentlyPlayed: List<Song> = listOf(
    Song("rp1","Forever Outsider","Evergrey","2021", R.drawable.spotixe_logo),
    Song("rp2","Leviathan","Mastodon","2020", R.drawable.spotixe_logo),
    Song("rp3","Blackbird","Alter Bridge","2007", R.drawable.spotixe_logo),
    Song("rp4","Forever Outsider","Evergrey","2021", R.drawable.spotixe_logo),
    Song("rp5","Leviathan","Mastodon","2020", R.drawable.spotixe_logo),
    Song("rp6","Blackbird","Alter Bridge","2007", R.drawable.spotixe_logo),
    Song("rp7","Forever Outsider","Evergrey","2021", R.drawable.spotixe_logo),
    Song("rp8","Leviathan","Mastodon","2020", R.drawable.spotixe_logo),
    Song("rp9","Blackbird","Alter Bridge","2007", R.drawable.spotixe_logo),
    Song("rp11","Forever Outsider","Evergrey","2021", R.drawable.spotixe_logo),
    Song("rp12","Forever Outsider","Evergrey","2021", R.drawable.spotixe_logo),
    Song("rp13","Forever Outsider","Evergrey","2021", R.drawable.spotixe_logo),
)


object SongRepository {
    val all: List<Song> get() = recentlyPlayed + topPicks
    fun get(id: String) = all.firstOrNull { it.id == id }
}

data class Playlist(
    val id: String,
    val title: String,
    val coverRes: Int,
    val songIds: List<String>
)

object PlaylistRepository {
    // ví dụ vài playlist
    val all = listOf(
        Playlist(
            id = "vp-top100",
            title = "Top 100 - V-pop",
            coverRes = R.drawable.spotixe_logo,
            songIds = listOf("rp1","rp2","rp3","rp4","rp5","rp6","rp7","rp8","rp9")
        ),
        Playlist(
            id = "rock-mix",
            title = "Rock Mix",
            coverRes = R.drawable.spotixe_logo,
            songIds = listOf("1","2","3","4","5","6","7","8","9")
        )
    )

    fun get(id: String) = all.firstOrNull { it.id == id }
    fun songsOf(pl: Playlist) = pl.songIds.mapNotNull { SongRepository.get(it) }
}


data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val coverRes: Int,
    val songIds: List<String> // map sang SongRepository
)

object AlbumRepository {
    // Dùng sẵn id từ SongRepository (recentlyPlayed + topPicks)
    val all = listOf(
        Album(
            id = "alb_highlights",
            title = "The highlights",
            artist = "The Weeknd",
            coverRes = R.drawable.spotixe_logo,           // đổi ảnh thật của bạn nếu có
            songIds = listOf("rp1","rp2","rp3","rp4","rp5","rp6","rp7","rp8","rp9")
        ),
        Album(
            id = "alb_rock_mix",
            title = "Rock Mix",
            artist = "Various",
            coverRes = R.drawable.spotixe_logo,
            songIds = listOf("1","2","3","4","5","6","7","8","9")
        )
    )

    fun get(id: String) = all.firstOrNull { it.id == id }
    fun songsOf(album: Album): List<Song> = album.songIds.mapNotNull { SongRepository.get(it) }
}

data class Artist(
    val id: String,
    val name: String,
    val coverRes: Int,
    val albumId: String?,
    val albumReleaseDate: String?,
    val topSongIds: List<String>
)

object ArtistRepository {
    val all = listOf(
        Artist(
            id = "artist_gem",
            name = "G.E.M",
            coverRes = R.drawable.spotixe_logo,
            albumId = "alb_gem_gloria",
            albumReleaseDate = "12 Jun 2025",
            topSongIds = listOf("rp1", "rp2", "rp3", "rp4", "rp5")
        ),
        Artist(
            id = "artist_weeknd",
            name = "The Weeknd",
            coverRes = R.drawable.spotixe_logo,
            albumId = "alb_highlights",
            albumReleaseDate = "2021",
            topSongIds = listOf("rp6", "rp7", "rp8", "rp9", "1")
        )
    )

    fun get(id: String) = all.firstOrNull { it.id == id }
}