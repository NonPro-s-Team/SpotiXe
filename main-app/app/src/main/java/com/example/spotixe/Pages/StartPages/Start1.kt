package com.example.spotixe.Pages.Pages.StartPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.spotixe.AuthRoute
import com.example.spotixe.R
import com.example.spotixe.StartRoute

@Composable
fun StartScreen(navController: NavController) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF031508),
                        Color(0xFF58BA47)
                    ),
                    start = Offset(1000f, 0f),
                    end = Offset(0f, 1800f)
                )
            )
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // Responsive sizes
        val logoHeight = screenHeight * 0.28f
        val titleFontSize = screenWidth.value * 0.10f
        val buttonHeight = screenHeight * 0.085f

        val topSpacer = screenHeight * 0.08f
        val smallSpacer = screenHeight * 0.03f
        val bigSpacer = screenHeight * 0.15f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(topSpacer))

            Image(
                painter = painterResource(R.drawable.spotixe_logo),
                contentDescription = null,
                modifier = Modifier.height(logoHeight)
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            Text(
                text = "Melody comes\nwith you all along",
                color = Color.White,
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = (titleFontSize * 1.2f).sp
            )

            Spacer(modifier = Modifier.height(bigSpacer))

            // BUTTON 1
            Button(
                onClick = { navController.navigate(AuthRoute.SignIn1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDDDDDD))
            ) {
                Text(
                    "I already have account",
                    color = Color.Black,
                    fontSize = (titleFontSize * 0.45f).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(smallSpacer))

            // BUTTON 2
            Button(
                onClick = { navController.navigate(StartRoute.Start2) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDDDDDD))
            ) {
                Text(
                    "Sign up free",
                    color = Color.Black,
                    fontSize = (titleFontSize * 0.45f).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
