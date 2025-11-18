package com.example.spotixe.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomBarNavItem(
    val label : String,
    val icon : ImageVector,
    val routes : String
)

object BottomBarNavData {
    val bottomBarItemList = listOf(
        BottomBarNavItem("Home", Icons.Filled.Home, "home"),
        BottomBarNavItem("Explore", Icons.Filled.Album, "explore"),
        BottomBarNavItem("Search", Icons.Filled.Search, "search"),
        BottomBarNavItem("User", Icons.Filled.AccountCircle, "user")
    )
}


