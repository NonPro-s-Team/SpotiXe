package com.example.spotixe.Pages.Pages.SignUpPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import Components.Layout.OtpInputField
import android.app.Activity
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spotixe.AuthRoute
import com.example.spotixe.Graph.AUTH
import com.example.spotixe.MainRoute
import com.example.spotixe.R
import com.example.spotixe.services.verifyOtpCode

@Composable
fun Sign_UpPhone2Screen(navController: NavController) {

    val green = Color(0xFF58BA47)

    val otpValue = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isResending by rememberSaveable { mutableStateOf(false) }
    var resendCountdown by rememberSaveable { mutableStateOf(0) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // -------- Responsive Values ---------
        val logoHeight = screenHeight * 0.22f
        val titleFontSize = screenWidth.value * 0.085f
        val subTitleFontSize = screenWidth.value * 0.060f
        val labelFontSize = screenWidth.value * 0.045f
        val smallFontSize = screenWidth.value * 0.038f

        val otpBoxSize = screenWidth * 0.12f

        val buttonWidth = screenWidth * 0.45f
        val buttonHeight = screenHeight * 0.065f

        val rowButtonHeight = screenHeight * 0.060f

        val smallSpacer = screenHeight * 0.02f
        val normalSpacer = screenHeight * 0.03f
        val bigSpacer = screenHeight * 0.05f
        // ------------------------------------

        // Back Button
        Row(
            modifier = Modifier
                .padding(start = 15.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(navController)
        }

        // MAIN CONTENT
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

            Spacer(modifier = Modifier.height(smallSpacer))

            Text(
                "Enter your OTP",
                fontSize = subTitleFontSize.sp,
                color = green,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            // ----------- OTP INPUT FIELD -----------
            OtpInputField(
                otp = otpValue,
                count = 6,
                mask = true,
                boxSize = otpBoxSize,      // responsive box size
                textSize = labelFontSize.sp,
                onFilled = { code ->
                    if (code.length == 6) {
                        isLoading = true
                        verifyOtpCode(
                            activity = activity,
                            code = code,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "OTP verified!", Toast.LENGTH_SHORT).show()
                                navController.navigate(AuthRoute.SignUpPhone3)
                            },
                            onError = { msg ->
                                isLoading = false
                                Toast.makeText(context, "Verification failed: $msg", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(normalSpacer))

            // Verify Button
            Button(
                onClick = {
                    val code = otpValue.value
                    when {
                        code.isEmpty() ->
                            Toast.makeText(context, "Please enter OTP", Toast.LENGTH_SHORT).show()

                        code.length != 6 ->
                            Toast.makeText(context, "OTP must be 6 digits", Toast.LENGTH_SHORT).show()

                        else -> {
                            isLoading = true
                            verifyOtpCode(
                                activity = activity,
                                code = code,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Verified!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(AuthRoute.SignUpPhone3)
                                },
                                onError = { msg ->
                                    isLoading = false
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                },
                enabled = !isLoading && otpValue.value.length == 6,
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isLoading) "Verifying..." else "Verify",
                    fontSize = labelFontSize.sp
                )
            }

            Spacer(modifier = Modifier.height(normalSpacer))

            // ------------- Resend + Change Number Row -------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Resend OTP Button
                Button(
                    onClick = {
                        if (resendCountdown > 0) {
                            Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isResending = true
                        Toast.makeText(context, "OTP resent", Toast.LENGTH_SHORT).show()
                        isResending = false
                        resendCountdown = 60
                    },
                    enabled = resendCountdown == 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(rowButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF444444),
                        contentColor = green
                    )
                ) {
                    Text(
                        text = if (resendCountdown > 0) "Resend (${resendCountdown}s)" else "Resend OTP",
                        fontSize = smallFontSize.sp
                    )
                }

                // Change number
                Button(
                    onClick = {
                        navController.popBackStack()
                        Toast.makeText(context, "Enter phone again", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(rowButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF444444),
                        contentColor = green
                    )
                ) {
                    Text(
                        "Change Number",
                        fontSize = smallFontSize.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(bigSpacer))

            Text(
                text = buildAnnotatedString {
                    append("Or ")
                    withStyle(style = SpanStyle(color = Color.White)) { append("sign up") }
                    append(" with")
                },
                color = green,
                fontSize = labelFontSize.sp
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
                        "Sign in failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(bigSpacer / 1.3f))

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
