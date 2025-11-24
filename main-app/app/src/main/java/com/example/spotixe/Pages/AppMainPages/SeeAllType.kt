package com.example.spotixe.Pages.Pages.AppMainPages

/**
 * Sealed class định nghĩa các loại "See All" screens
 * Mỗi type tương ứng với 1 loại dữ liệu (Song, Playlist, Album, Artist)
 */
sealed class SeeAllType(val value: String) {
    data object RecentlyPlayed : SeeAllType("recently_played")
    data object Playlists : SeeAllType("playlists")
    data object Albums : SeeAllType("albums")
    data object Artists : SeeAllType("artists")

    companion object {
        fun from(value: String): SeeAllType {
            return when (value) {
                RecentlyPlayed.value -> RecentlyPlayed
                Playlists.value -> Playlists
                Albums.value -> Albums
                Artists.value -> Artists
                else -> RecentlyPlayed // Default
            }
        }

        fun toTitle(type: SeeAllType): String {
            return when (type) {
                RecentlyPlayed -> "Recently Played"
                Playlists -> "All Playlists"
                Albums -> "All Albums"
                Artists -> "All Artists"
            }
        }
    }
}

