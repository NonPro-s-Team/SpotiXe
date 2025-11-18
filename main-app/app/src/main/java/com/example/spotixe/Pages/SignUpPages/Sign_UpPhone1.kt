package com.example.spotixe.Pages.Pages.SignUpPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spotixe.AuthRoute
import com.example.spotixe.Graph
import com.example.spotixe.Graph.AUTH
import com.example.spotixe.MainRoute
import com.example.spotixe.R
import com.example.spotixe.services.startPhoneVerification
import com.example.spotixe.services.normalizeVietnamPhone

@Composable
fun Sign_UpPhone1Screen(navController: NavController) {

    val green = Color(0xFF58BA47)
    val context = LocalContext.current
    val activity = context as Activity

    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // ------------ Responsive values -------------
        val logoHeight = screenHeight * 0.22f
        val titleFontSize = screenWidth.value * 0.085f
        val labelFontSize = screenWidth.value * 0.045f
        val inputFontSize = screenWidth.value * 0.040f
        val alertFontSize = screenWidth.value * 0.030f

        val buttonWidth = screenWidth * 0.45f
        val buttonHeight = screenHeight * 0.065f

        val smallSpacer = screenHeight * 0.02f
        val normalSpacer = screenHeight * 0.03f
        val bigSpacer = screenHeight * 0.05f
        // --------------------------------------------

        // Back button
        Row(
            modifier = Modifier
                .padding(start = 15.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(navController)
        }

        // --------- MAIN COLUMN ---------
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
                "Create your account",
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(bigSpacer))

            // Phone label
            Text(
                text = "Phone number",
                color = green,
                fontSize = labelFontSize.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(smallSpacer / 1.3f))

            // Phone input
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                textStyle = TextStyle(color = green, fontSize = inputFontSize.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF444444),
                    unfocusedContainerColor = Color(0xFF444444),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = green,
                    unfocusedTextColor = green,
                    cursorColor = green,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            // Continue button (send OTP)
            Button(
                onClick = {
                    if (phoneNumber.isEmpty()) {
                        Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    val normalizedPhone = normalizeVietnamPhone(phoneNumber)

                    if (!normalizedPhone.startsWith("+84") || normalizedPhone.length < 11) {
                        isLoading = false
                        errorMessage = "Invalid phone number. Format: 0xxxxxxxxx or +84xxxxxxxxx"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    startPhoneVerification(
                        activity = activity,
                        rawPhone = normalizedPhone,
                        onCodeSent = {
                            isLoading = false
                            Toast.makeText(context, "Verification code sent", Toast.LENGTH_SHORT).show()
                            navController.navigate(AuthRoute.SignUpPhone2)
                        },
                        onError = { msg ->
                            isLoading = false
                            errorMessage = msg
                            Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !isLoading && phoneNumber.isNotBlank(),
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isLoading) "Sending..." else "Continue",
                    fontSize = labelFontSize.sp
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(smallSpacer))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = alertFontSize.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(bigSpacer))

            // OR Text
            Text(
                text = buildAnnotatedString {
                    append("Or ")
                    withStyle(SpanStyle(color = Color.White)) { append("sign up") }
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
                onError = { error ->
                    Toast.makeText(
                        context,
                        "Sign in failed: ${error.message ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(bigSpacer))

            // Already have account?
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = green)) { append("Already have account?\n") }
                    withStyle(SpanStyle(color = green)) { append("Click here to ") }
                    withStyle(SpanStyle(color = Color.White, fontStyle = FontStyle.Italic)) {
                        append("sign in")
                    }
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
