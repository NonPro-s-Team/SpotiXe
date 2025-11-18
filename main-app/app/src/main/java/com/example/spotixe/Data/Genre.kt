package com.example.spotixe.Data

import androidx.compose.ui.graphics.Color
import com.example.spotixe.R

data class Genre(
    val title: String,
    val color: Color,
    val imageRes: Int
)

val genres = listOf(
    Genre("V-Pop", Color(0xFF2F6BFF), R.drawable.spotixe_logo),
    Genre("Hard Rock", Color(0xFFE05A33), R.drawable.spotixe_logo),
    Genre("Rap", Color(0xFFD54735), R.drawable.spotixe_logo),
    Genre("Indie", Color(0xFFCA34B7), R.drawable.spotixe_logo),
    Genre("Lofi", Color(0xFF19C16A), R.drawable.spotixe_logo),
    Genre("Jazz", Color(0xFFE0B81A), R.drawable.spotixe_logo),
    Genre("Acoustic", Color(0xFFCBEA3E), R.drawable.spotixe_logo),
    Genre("Punk", Color(0xFFFF2D6A), R.drawable.spotixe_logo),
    Genre("K-Pop", Color(0xFFD41AFF), R.drawable.spotixe_logo),
    Genre("EDM", Color(0xFF00E5FF), R.drawable.spotixe_logo),
    Genre("Hip-Hop", Color(0xFF9B59B6), R.drawable.spotixe_logo),
    Genre("Future Bass", Color(0xFF1ABC9C), R.drawable.spotixe_logo),
    Genre("R&B", Color(0xFF8E44AD), R.drawable.spotixe_logo),
    Genre("Soul", Color(0xFF6A1B9A), R.drawable.spotixe_logo),
    Genre("Chill", Color(0xFF26A69A), R.drawable.spotixe_logo),
    Genre("Dance", Color(0xFF00C853), R.drawable.spotixe_logo),
    Genre("Classical", Color(0xFF795548), R.drawable.spotixe_logo),
    Genre("Metal", Color(0xFF263238), R.drawable.spotixe_logo),
    Genre("Blues", Color(0xFF2962FF), R.drawable.spotixe_logo),
    Genre("Country", Color(0xFFA67C52), R.drawable.spotixe_logo),
    Genre("Latin", Color(0xFFFF7043), R.drawable.spotixe_logo),
    Genre("Reggae", Color(0xFF2E7D32), R.drawable.spotixe_logo),
    Genre("House", Color(0xFF00B8D4), R.drawable.spotixe_logo),
    Genre("Dubstep", Color(0xFF7B1FA2), R.drawable.spotixe_logo),
    Genre("Trap", Color(0xFF880E4F), R.drawable.spotixe_logo),
    Genre("Country Pop", Color(0xFFFFC107), R.drawable.spotixe_logo),
    Genre("Orchestra", Color(0xFF6D4C41), R.drawable.spotixe_logo),
    Genre("Folk", Color(0xFF4CAF50), R.drawable.spotixe_logo),
    Genre("Synthwave", Color(0xFFFF2D95), R.drawable.spotixe_logo),
    Genre("Ambient", Color(0xFF18FFFF), R.drawable.spotixe_logo)
)

