package com.example.spotixe.Pages.Pages.SignInPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spotixe.AuthRoute
import com.example.spotixe.MainRoute
import com.example.spotixe.Graph.AUTH
import com.example.spotixe.R

@Composable
fun Sign_in1Screen(
    navController: NavController
){
    val green = Color(0xFF58BA47)
    var emailorphone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF121212)
            )
    )
    {
        Row (
                modifier = Modifier
                    .padding(start = 15.dp)
                    .statusBarsPadding(),
            horizontalArrangement = Arrangement.Start
        ){
            BackButton(navController)
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Image(
                painter = painterResource(R.drawable.spotixe_logo),
                contentDescription = null,
                modifier = Modifier.height(180.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Sign in your account",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Email label
            Text(
                text = "Email or Phone number",
                color = green,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(8.dp))

            // TextField cho Email
            TextField(
                value = emailorphone,
                onValueChange = {
                    emailorphone = it
                },
                textStyle = TextStyle(color = green, fontSize = 16.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF444444),
                    unfocusedContainerColor = Color(0xFF444444),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = green,
                    unfocusedTextColor = green,
                    cursorColor = green,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(20.dp))

            // Password label
            Text(
                text = "Password",
                color = green,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(8.dp))

            // TextField cho Password - with toggle visibility
            TextField(
                value = password,
                onValueChange = {
                    password = it
                },
                textStyle = TextStyle(color = green, fontSize = 16.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF444444),
                    unfocusedContainerColor = Color(0xFF444444),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = green,
                    unfocusedTextColor = green,
                    cursorColor = green
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = green
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Forgot password",
                color = Color.White,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable {
                        // Navigate to forgot password screen if available
                        // Placeholder for forgot password functionality
                    }
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    // Validate email/phone and password before signing in
                    if (emailorphone.isNotEmpty() && password.isNotEmpty()) {
                        // TODO: Call API to sign in
                        // Navigate to Home on success
                        navController.navigate(MainRoute.Home) {
                            popUpTo(AUTH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .width(150.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )

            ) {
                Text(
                    text = "Sign in",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = buildAnnotatedString {
                    append("Or ")
                    withStyle(style = SpanStyle(color = Color.White)) { append("sign in") }
                    append(" with")
                },
                color = green,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            GoogleSignInButtonFirebase(
                onSuccess = { loginResponse ->
                    // Successfully logged in with JWT saved - navigate directly to Home
                    navController.navigate(MainRoute.Home) {
                        popUpTo(AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { error ->
                    // Error handled with Toast in the button
                }
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = green)) {append("Don't have account ?\n")}
                    withStyle(style = SpanStyle(color = green)) { append("Click here to ") }
                    withStyle(style = SpanStyle(color = Color.White, fontStyle = FontStyle.Italic)) { append("sign up") }
                },
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.navigate(AuthRoute.SignUpEmail1)
                }
            )
        }
    }
}

