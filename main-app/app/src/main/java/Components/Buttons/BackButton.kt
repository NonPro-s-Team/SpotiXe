package Components.Buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@Composable
fun BackButton(navController: NavController){
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(40.dp)
            .clickable { navController.navigateUp() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = "Back",
            tint = Color(0xFF58BA47),
            modifier = Modifier
                .size(40.dp)
                .padding(6.dp)
        )
    }
}