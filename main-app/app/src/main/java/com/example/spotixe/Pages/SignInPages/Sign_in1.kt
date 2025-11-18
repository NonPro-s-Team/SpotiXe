package com.example.spotixe.Pages.Pages.SignInPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
) {
    val green = Color(0xFF58BA47)

    var emailorphone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // --- Reduced Responsive sizes (optimized) ---
        val logoHeight = screenHeight * 0.15f            // giảm từ 0.23f
        val titleFontSize = screenWidth.value * 0.070f    // giảm từ 0.085f
        val labelFontSize = screenWidth.value * 0.040f
        val inputFontSize = screenWidth.value * 0.036f

        val buttonWidth = screenWidth * 0.70f             // tăng chiều rộng cho đẹp
        val buttonHeight = screenHeight * 0.055f          // giảm chiều cao button

        val smallSpacer = screenHeight * 0.015f
        val normalSpacer = screenHeight * 0.020f
        val bigSpacer = screenHeight * 0.035f              // giảm 30% khoảng cách
        // ---------------------------------------------------

        Row(
            modifier = Modifier
                .padding(start = 15.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(navController)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // KEY: auto balance các phần
        ) {

            // ========= TOP SECTION =========
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(smallSpacer))

                Image(
                    painter = painterResource(R.drawable.spotixe_logo),
                    contentDescription = null,
                    modifier = Modifier.height(logoHeight)
                )

                Spacer(modifier = Modifier.height(normalSpacer))

                Text(
                    "Sign in your account",
                    fontSize = titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = green,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(normalSpacer))

                // EMAIL LABEL
                Text(
                    text = "Email or Phone number",
                    color = green,
                    fontSize = labelFontSize.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(smallSpacer))

                TextField(
                    value = emailorphone,
                    onValueChange = { emailorphone = it },
                    textStyle = TextStyle(color = green, fontSize = inputFontSize.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF444444),
                        unfocusedContainerColor = Color(0xFF444444),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = green,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(normalSpacer))

                // PASSWORD LABEL
                Text(
                    text = "Password",
                    color = green,
                    fontSize = labelFontSize.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(smallSpacer))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    textStyle = TextStyle(color = green, fontSize = inputFontSize.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF444444),
                        unfocusedContainerColor = Color(0xFF444444),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = green
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, null, tint = green)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(smallSpacer))

                Text(
                    "Forgot password",
                    color = Color.White,
                    fontSize = (labelFontSize * 0.9f).sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            // ========= MIDDLE ACTION SECTION =========
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Button(
                    onClick = {
                        if (emailorphone.isNotEmpty() && password.isNotEmpty()) {
                            navController.navigate(MainRoute.Home) {
                                popUpTo(AUTH) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(buttonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = green,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Sign in", fontSize = (labelFontSize * 1.1f).sp)
                }

                Spacer(modifier = Modifier.height(normalSpacer))

                Text(
                    text = buildAnnotatedString {
                        append("Or ")
                        withStyle(SpanStyle(color = Color.White)) { append("sign in") }
                        append(" with")
                    },
                    color = green,
                    fontSize = inputFontSize.sp
                )

                Spacer(modifier = Modifier.height(smallSpacer))

                GoogleSignInButtonFirebase(
                    onSuccess = {
                        navController.navigate(MainRoute.Home) {
                            popUpTo(AUTH) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onError = {}
                )
            }

            // ========= BOTTOM SECTION =========
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = green)) { append("Don't have account?\n") }
                    withStyle(SpanStyle(color = green)) { append("Click here to ") }
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                    ) { append("sign up") }
                },
                fontSize = labelFontSize.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = smallSpacer)
                    .clickable { navController.navigate(AuthRoute.SignUpEmail1) }
            )
        }
    }
}



