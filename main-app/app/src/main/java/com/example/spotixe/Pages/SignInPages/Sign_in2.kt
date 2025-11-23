package com.example.spotixe.Pages.Pages.SignInPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import Components.Layout.OtpInputField
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spotixe.Graph.AUTH
import com.example.spotixe.MainRoute
import com.example.spotixe.R
import com.example.spotixe.auth.data.repository.AuthViewModelFactory
import com.example.spotixe.auth.viewmodel.AuthViewModel
import com.example.spotixe.viewmodel.SignUpViewModel

@Composable
fun Sign_in2Screen(
    navController: NavController
) {
    val green = Color(0xFF58BA47)
    val context = LocalContext.current
    val email = SignUpViewModel.loadEmail()

    var otp by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )

    val verifyState by authViewModel.verifyState.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        val logoHeight = screenHeight * 0.22f
        val titleFontSize = screenWidth.value * 0.085f
        val labelFontSize = screenWidth.value * 0.045f
        val inputFontSize = screenWidth.value * 0.040f
        val bigSpacer = screenHeight * 0.05f
        val normalSpacer = screenHeight * 0.03f
        val smallSpacer = screenHeight * 0.02f

        Row(
            modifier = Modifier
                .padding(start = 15.dp)
                .statusBarsPadding(),
        ) {
            BackButton(navController)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(bigSpacer))

            Image(
                painter = painterResource(R.drawable.spotixe_logo),
                contentDescription = null,
                modifier = Modifier.height(logoHeight)
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            Text(
                "Verify Your Email",
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            Text(
                text = "We have sent a 6-digit OTP to:",
                color = Color.White,
                fontSize = inputFontSize.sp
            )

            Text(
                text = email,
                color = green,
                fontSize = (inputFontSize * 1.2f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            Text(
                text = "Enter the 6-digit code",
                color = green,
                fontSize = labelFontSize.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(smallSpacer))

            OtpInputField(
                otp = remember { mutableStateOf("") },
                count = 6,
                mask = false,
                boxSize = 48.dp,
                textSize = 20.sp,
                onFilled = { code ->
                    otp = code
                }
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            // ------------------ VERIFY BUTTON ------------------
            Button(
                onClick = {
                    if (otp.length < 6) {
                        Toast.makeText(context, "Please enter full 6-digit OTP", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.verifyOtp(email, otp)
                        isLoading = true
                    }
                },
                modifier = Modifier
                    .width(screenWidth * 0.45f)
                    .height(screenHeight * 0.065f),
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
                    Text(text = "Sign In", fontSize = labelFontSize.sp)
                }
            }

            LaunchedEffect(verifyState) {
                verifyState?.let { resp ->
                    if (resp.success) {
                        // Lưu JWT token và user data vào DataStore
                        authViewModel.saveAuthDataFromOtp(resp)

                        // Hiển thị thông báo thành công
                        Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()

                        // Điều hướng sang Home
                        navController.navigate(MainRoute.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        isLoading = false
                        Toast.makeText(context, "Invalid OTP. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(normalSpacer))

            // Resend OTP
            Text(
                text = "Didn't receive the code? Tap to resend.",
                color = Color.Gray,
                fontSize = inputFontSize.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        authViewModel.requestOtp(email)
                        Toast.makeText(context, "OTP resent to your email", Toast.LENGTH_SHORT).show()
                    }
                    .padding(5.dp)
            )

            Spacer(modifier = Modifier.height(bigSpacer))

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

            Spacer(modifier = Modifier.height(bigSpacer))
        }
    }
}

