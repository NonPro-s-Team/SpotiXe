package com.example.spotixe.Pages.Pages.StartPages

import Components.Buttons.GoogleSignInButtonFirebase
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.spotixe.AuthRoute
import com.example.spotixe.Graph.AUTH
import com.example.spotixe.MainRoute
import com.example.spotixe.R

@Composable
fun Start2Screen(navController: NavController) {
    val context = LocalContext.current

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
        val titleFontSize = screenWidth.value * 0.10f          // responsive text
        val buttonHeight = screenHeight * 0.085f
        val bigSpacer = screenHeight * 0.15f
        val smallSpacer = screenHeight * 0.03f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(smallSpacer))

            Image(
                painter = painterResource(R.drawable.spotixe_logo),
                contentDescription = null,
                modifier = Modifier.height(logoHeight)
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            Text(
                "Melody comes\nwith you all along",
                color = Color.White,
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = (titleFontSize * 1.2f).sp
            )

            Spacer(modifier = Modifier.height(bigSpacer))

            // ----- BUTTON 1 -----
            ResponsiveButton(
                label = "Continue with phone",
                height = buttonHeight,
                onClick = { navController.navigate(AuthRoute.SignUpPhone1) }
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            // ----- BUTTON 2 -----
            ResponsiveButton(
                label = "Continue with email",
                height = buttonHeight,
                onClick = { navController.navigate(AuthRoute.SignIn1) }
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            // ----- BUTTON 3 Google with Firebase Auth -----
            GoogleSignInButtonFirebase(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                containerColor = Color(0xFFDDDDDD),
                cornerRadius = 28,
                onSuccess = { loginResponse ->
                    // Successfully logged in - navigate to Home
                    Toast.makeText(context, "Welcome ${loginResponse.user.username}!", Toast.LENGTH_SHORT).show()
                    navController.navigate(MainRoute.Home) {
                        popUpTo(AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { error ->
                    // Error already handled with Toast in the component
                    Toast.makeText(
                        context,
                        "Sign in failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
fun ResponsiveButton(label: String, height: Dp, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDDDDDD))
    ) {
        Text(
            label,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
