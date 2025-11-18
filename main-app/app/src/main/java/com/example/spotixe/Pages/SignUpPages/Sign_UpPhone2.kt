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
fun Sign_UpPhone2Screen(navController: NavController){
    val green = Color(0xFF58BA47)
    var otpValue = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isResending by rememberSaveable { mutableStateOf(false) }
    var resendCountdown by rememberSaveable { mutableStateOf(0) }
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
                "Create your account",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Enter your OTP",
                fontSize = 25.sp,
                color = green,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(15.dp))

            OtpInputField(
                otp = otpValue,
                count = 6,
                mask = true,
                onFilled = { code ->
                    if (code.length == 6) {
                        isLoading = true
                        verifyOtpCode(
                            activity = activity,
                            code = code,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "OTP verified successfully!", Toast.LENGTH_SHORT).show()
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


            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (otpValue.value.isEmpty()) {
                        Toast.makeText(context, "Please enter OTP code", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (otpValue.value.length != 6) {
                        Toast.makeText(context, "OTP code must be 6 digits", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    verifyOtpCode(
                        activity = activity,
                        code = otpValue.value,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "OTP verified successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate(AuthRoute.SignUpPhone3)
                        },
                        onError = { msg ->
                            isLoading = false
                            Toast.makeText(context, "Verification failed: $msg", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !isLoading && otpValue.value.length == 6,
                modifier = Modifier
                    .width(150.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isLoading) "Verifying..." else "Verify",
                    fontSize = 18.sp
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            // Resend and Change Phone buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Resend OTP Button
                Button(
                    onClick = {
                        if (isResending || resendCountdown > 0) {
                            Toast.makeText(context, "Please wait before resending", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isResending = true
                        // TODO: Call resend OTP API here
                        Toast.makeText(context, "OTP code resent successfully", Toast.LENGTH_SHORT).show()
                        isResending = false
                        resendCountdown = 60
                    },
                    enabled = !isResending && resendCountdown == 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF444444),
                        contentColor = green
                    )
                ) {
                    Text(
                        text = if (resendCountdown > 0) "Resend (${resendCountdown}s)" else "Resend OTP",
                        fontSize = 14.sp
                    )
                }

                // Change Phone Number Button
                Button(
                    onClick = {
                        navController.popBackStack()
                        Toast.makeText(context, "Please enter your phone number again", Toast.LENGTH_SHORT).show()
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF444444),
                        contentColor = green
                    )
                ) {
                    Text(
                        text = "Change Number",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = buildAnnotatedString {
                    append("Or ")
                    withStyle(style = SpanStyle(color = Color.White)) { append("sign up") }
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
                    // Show error message when Google Sign In fails
                    Toast.makeText(
                        context,
                        "Sign in failed: ${error.message ?: "Unknown error occurred"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = green)) {append("Already have account ?\n")}
                    withStyle(style = SpanStyle(color = green)) { append("Click here to ") }
                    withStyle(style = SpanStyle(color = Color.White, fontStyle = FontStyle.Italic)) { append("sign in") }
                },
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { navController.navigate(AuthRoute.SignIn1) }
            )


        }

    }
}