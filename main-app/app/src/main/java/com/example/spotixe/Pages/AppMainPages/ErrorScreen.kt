package com.example.spotixe.Pages.Pages.AppMainPages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotixe.R

@Composable
fun ErrorScreen(
    message: String = "Something went wrong!",
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // nền đen style app nghe nhạc
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ICON LỖI
            Icon(
                painter = painterResource(id = R.drawable.heart), // bạn nhớ tạo icon này trong drawable
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // TITLE
            Text(
                text = "Oops!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            // NỘI DUNG LỖI
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // BUTTON TRY AGAIN
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DB954) // màu xanh Spotify
                ),
                modifier = Modifier
                    .height(48.dp)
                    .width(150.dp)
            ) {
                Text(
                    text = "Try Again",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
