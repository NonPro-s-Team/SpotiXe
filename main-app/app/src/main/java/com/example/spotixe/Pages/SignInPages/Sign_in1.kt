package com.example.spotixe.Pages.Pages.SignInPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spotixe.AuthRoute
import com.example.spotixe.MainRoute
import com.example.spotixe.Graph.AUTH
import com.example.spotixe.R
import com.example.spotixe.auth.data.repository.AuthViewModelFactory
import com.example.spotixe.auth.viewmodel.AuthViewModel
import com.example.spotixe.viewmodel.SignUpViewModel

@Composable
fun Sign_in1Screen(
    navController: NavController
) {
    val green = Color(0xFF58BA47)
    val context = LocalContext.current

    // Init SignUpViewModel
    LaunchedEffect(Unit) {
        SignUpViewModel.init(context)
    }

    var email by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    // AuthViewModel để gọi API request OTP
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )
    val otpState by authViewModel.otpState.collectAsState()

    // Lắng nghe kết quả request OTP
    LaunchedEffect(otpState) {
        when (otpState) {
            "success" -> {
                isLoading = false
                Toast.makeText(context, "OTP sent to your email!", Toast.LENGTH_SHORT).show()
                // Lưu email để dùng ở màn hình Sign_in2
                SignUpViewModel.saveEmail(email)
                // Chuyển sang màn hình nhập OTP
                navController.navigate(AuthRoute.SignIn2)
            }
            "error", null -> {
                // Không làm gì khi null (trạng thái ban đầu)
            }
            else -> {
                // Có lỗi
                isLoading = false
                Toast.makeText(context, "Failed to send OTP: $otpState", Toast.LENGTH_LONG).show()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        val logoHeight = screenHeight * 0.15f
        val titleFontSize = screenWidth.value * 0.070f
        val labelFontSize = screenWidth.value * 0.040f
        val inputFontSize = screenWidth.value * 0.036f
        val buttonWidth = screenWidth * 0.70f
        val buttonHeight = screenHeight * 0.055f
        val smallSpacer = screenHeight * 0.015f
        val normalSpacer = screenHeight * 0.020f
        val bigSpacer = screenHeight * 0.035f

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
            verticalArrangement = Arrangement.SpaceBetween
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
                    text = "Email",
                    color = green,
                    fontSize = labelFontSize.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(smallSpacer))

                // Validate Email
                val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                val isEmailValid = emailRegex.matches(email)

                TextField(
                    value = email,
                    onValueChange = { email = it },
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
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = { Text("Enter your email", color = Color.LightGray) }
                )

                if (email.isNotEmpty() && !isEmailValid) {
                    Text(
                        text = "Please enter a valid email address.",
                        color = Color.Red,
                        fontSize = (inputFontSize * 0.75).sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(normalSpacer))

                Text(
                    text = "We'll send you a verification code to this email.",
                    color = Color.Gray,
                    fontSize = (inputFontSize * 0.9f).sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            // ========= MIDDLE ACTION SECTION =========
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Button(
                    onClick = {
                        if (email.isEmpty()) {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        } else if (!email.contains("@")) {
                            Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                        } else {
                            // Gọi API request OTP
                            isLoading = true
                            authViewModel.requestOtp(email)
                        }
                    },
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(buttonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = green,
                        contentColor = Color.Black
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Continue", fontSize = (labelFontSize * 1.1f).sp)
                    }
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
                    onError = { error ->
                        Toast.makeText(
                            context,
                            "Sign in failed: ${error.message ?: "Unknown error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
