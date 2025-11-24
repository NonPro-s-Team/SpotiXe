package com.example.spotixe.Pages.Pages.SignUpPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import Components.Layout.SpotixeDialog
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.spotixe.auth.data.api.AuthApiService
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
import com.example.spotixe.auth.data.api.RetrofitClient
import com.example.spotixe.auth.data.models.RequestOtpRequest
import com.example.spotixe.auth.data.repository.AuthRepository
import com.example.spotixe.auth.data.repository.AuthViewModelFactory
import com.example.spotixe.auth.viewmodel.AuthViewModel
import com.example.spotixe.viewmodel.SignUpViewModel
import com.google.android.gms.auth.api.Auth

@Composable
fun Sign_UpEmail1Screen(
    navController: NavController
) {
    val green = Color(0xFF58BA47)
    val context = LocalContext.current

    // Init SignUpViewModel khi screen load
    LaunchedEffect(Unit) {
        SignUpViewModel.init(context)
    }

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    // Request otp
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )

    val otpState by authViewModel.otpState.collectAsState()
    val otpErrorMessage by authViewModel.otpErrorMessage.collectAsState()

    // State để hiển thị dialog
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorDialogMessage by rememberSaveable { mutableStateOf("") }

    // State để hiển thị dialog cho Google Sign-In
    var showGoogleErrorDialog by rememberSaveable { mutableStateOf(false) }
    var googleErrorDialogMessage by rememberSaveable { mutableStateOf("") }

    // Lắng nghe kết quả request OTP
    LaunchedEffect(otpState) {
        when (otpState) {
            "success" -> {
                // Navigate to OTP verification screen
                navController.navigate(AuthRoute.SignUpEmail2) {
                    launchSingleTop = true
                }
            }
            "error" -> {
                // Hiển thị dialog với message từ API
                errorDialogMessage = otpErrorMessage ?: "Failed to send OTP"
                showErrorDialog = true
            }
            null -> {
                // Không làm gì khi null (trạng thái ban đầu)
            }
        }
    }

    // Error Dialog
    SpotixeDialog(
        visible = showErrorDialog,
        title = "Lỗi đăng ký",
        message = errorDialogMessage,
        primaryButtonText = "OK",
        onPrimaryClick = {
            showErrorDialog = false
            authViewModel.clearOtpError()
        },
        onDismissRequest = {
            showErrorDialog = false
            authViewModel.clearOtpError()
        }
    )

    // Google Sign-In Error Dialog
    SpotixeDialog(
        visible = showGoogleErrorDialog,
        title = "Lỗi đăng nhập Google",
        message = googleErrorDialogMessage,
        primaryButtonText = "OK",
        onPrimaryClick = {
            showGoogleErrorDialog = false
        },
        onDismissRequest = {
            showGoogleErrorDialog = false
        }
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // Responsive Sizes
        val logoHeight = screenHeight * 0.22f
        val titleFontSize = screenWidth.value * 0.085f
        val labelFontSize = screenWidth.value * 0.045f
        val inputFontSize = screenWidth.value * 0.040f
        val buttonWidth = screenWidth * 0.45f
        val buttonHeight = screenHeight * 0.065f
        val normalSpacer = screenHeight * 0.02f
        val bigSpacer = screenHeight * 0.05f

        // Back Button
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
                .padding(horizontal = 30.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(normalSpacer * 2))

            Image(
                painter = painterResource(R.drawable.spotixe_logo),
                contentDescription = null,
                modifier = Modifier.height(logoHeight)
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            Text(
                "Create your account",
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(bigSpacer / 1.3f))

            // -------------------- NAME --------------------
            Text(
                text = "Name",
                color = green,
                fontSize = labelFontSize.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(normalSpacer / 1.2f))

// Validate Name
            val isNameValid = name.length >= 3 &&
                    name.length <= 40 &&
                    name.any { it.isLetter() } &&
                    name.all { it.isLetter() || it.isWhitespace() }

            TextField(
                value = name,
                onValueChange = { name = it },
                textStyle = TextStyle(color = green, fontSize = inputFontSize.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF444444),
                    unfocusedContainerColor = Color(0xFF444444),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = green,
                    unfocusedTextColor = green,
                    cursorColor = green
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("Enter your name", color = Color.LightGray) }
            )

            if (name.isNotEmpty() && !isNameValid) {
                Text(
                    text = "Name must be 3–40 characters and contain only letters.",
                    color = Color.Red,
                    fontSize = (inputFontSize * 0.75).sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(normalSpacer * 1.5f))


// -------------------- EMAIL --------------------
            Text(
                text = "Email",
                color = green,
                fontSize = labelFontSize.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(normalSpacer / 1.2f))

            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            val isEmailValid = emailRegex.matches(email)

            TextField(
                value = email,
                onValueChange = { email = it.trim() },
                textStyle = TextStyle(color = green, fontSize = inputFontSize.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF444444),
                    unfocusedContainerColor = Color(0xFF444444),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = green,
                    unfocusedTextColor = green,
                    cursorColor = green
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("Enter your email", color = Color.LightGray) },
                singleLine = true
            )

            if (email.isNotEmpty() && !isEmailValid) {
                Text(
                    text = "Please enter a valid email address.",
                    color = Color.Red,
                    fontSize = (inputFontSize * 0.75).sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(bigSpacer / 2f))


            // SIGN UP BUTTON
            Button(
                onClick = {
                    when {
                        name.isEmpty() -> {
                            Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                        }
                        email.isEmpty() -> {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        }
                        !email.contains("@") -> {
                            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            SignUpViewModel.setData(email, name)
                            // Request OTP
                            authViewModel.requestOtp(email)

                            // Navigate to OTP verification screen
                            navController.navigate(AuthRoute.SignUpEmail2) {
                                launchSingleTop = true
                            }
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
                Text(
                    text = "Sign up",
                    fontSize = (labelFontSize * 1.1f).sp
                )
            }



            Spacer(modifier = Modifier.height(bigSpacer))

            Text(
                text = buildAnnotatedString {
                    append("Or ")
                    withStyle(SpanStyle(color = Color.White)) { append("Continue") }
                    append(" with")
                },
                color = green,
                fontSize = inputFontSize.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            GoogleSignInButtonFirebase(
                onSuccess = {
                    navController.navigate(MainRoute.Home) {
                        popUpTo(AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { errorMessage, errorCode ->
                    // Hiển thị dialog lỗi cho Google Sign-In
                    googleErrorDialogMessage = errorMessage
                    showGoogleErrorDialog = true
                }
            )

            Spacer(modifier = Modifier.height(normalSpacer * 2))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = green)) { append("Already have account?\n") }
                    withStyle(SpanStyle(color = green)) { append("Click here to ") }
                    withStyle(SpanStyle(color = Color.White, fontStyle = FontStyle.Italic)) { append("sign in") }
                },
                fontSize = labelFontSize.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.navigate(AuthRoute.SignIn1)
                }
            )
        }
    }
}