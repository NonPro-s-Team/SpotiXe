package com.example.spotixe.Pages.Pages.SignUpPages

import Components.Buttons.BackButton
import Components.Layout.OtpInputField
import Components.isValidPassword
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spotixe.AuthRoute
import com.example.spotixe.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

@Composable
fun Sign_UpPhone3Screen(navController: NavController) {

    val green = Color(0xFF58BA47)
    val context = LocalContext.current
    val currentUser = Firebase.auth.currentUser
    val phone = currentUser?.phoneNumber ?: ""

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repassword by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rePasswordVisible by rememberSaveable { mutableStateOf(false) }

    val isPasswordValid = isValidPassword(password)
    val isRePasswordMatch = repassword.isNotEmpty() && repassword == password

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {

        val screenHeight = maxHeight
        val screenWidth  = maxWidth

        // -------- Responsive values --------
        val logoHeight = screenHeight * 0.22f

        val headerFont = screenWidth.value * 0.060f      // ~20sp
        val labelFont  = screenWidth.value * 0.045f      // ~16–18sp
        val inputFont  = screenWidth.value * 0.040f      // ~16sp
        val warningFont = screenWidth.value * 0.032f     // ~12–13sp

        val buttonWidth = screenWidth * 0.45f
        val buttonHeight = screenHeight * 0.065f

        val smallSpacer = screenHeight * 0.02f
        val normalSpacer = screenHeight * 0.03f
        val bigSpacer = screenHeight * 0.05f
        // ----------------------------------

        // Back button
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

            Spacer(Modifier.height(bigSpacer))

            Image(
                painter = painterResource(R.drawable.spotixe_logo),
                contentDescription = null,
                modifier = Modifier.height(logoHeight)
            )

            Spacer(Modifier.height(smallSpacer))

            Text(
                "Enter Username and\nPassword to continue",
                fontSize = headerFont.sp,
                color = green,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // ---------------- USERNAME ----------------
            Spacer(Modifier.height(normalSpacer))

            Text(
                text = "Username",
                color = green,
                fontSize = labelFont.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(smallSpacer / 1.3f))

            TextField(
                value = username,
                onValueChange = { username = it },
                textStyle = TextStyle(color = green, fontSize = inputFont.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF444444),
                    unfocusedContainerColor = Color(0xFF444444),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = green
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                singleLine = true
            )

            // ---------------- PASSWORD ----------------
            Spacer(Modifier.height(normalSpacer))

            Text(
                text = "Password",
                color = green,
                fontSize = labelFont.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(smallSpacer / 1.3f))

            Column {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    textStyle = TextStyle(color = green, fontSize = inputFont.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF444444),
                        unfocusedContainerColor = Color(0xFF444444),
                        focusedIndicatorColor = if (isPasswordValid) Color.Transparent else Color.Red,
                        unfocusedIndicatorColor = if (isPasswordValid) Color.Transparent else Color.Red,
                        cursorColor = green
                    ),
                    placeholder = { Text("Enter password", color = Color.LightGray, fontSize = inputFont.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )

                if (!isPasswordValid && password.isNotEmpty()) {
                    Text(
                        text = "Password must have 8 characters, uppercase, lowercase and a number",
                        color = Color.Red,
                        fontSize = warningFont.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }

            // ------------ CONFIRM PASSWORD ------------
            Spacer(Modifier.height(normalSpacer))

            Text(
                text = "Confirm your password",
                color = green,
                fontSize = labelFont.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(smallSpacer / 1.3f))

            Column {
                TextField(
                    value = repassword,
                    onValueChange = { repassword = it },
                    textStyle = TextStyle(color = green, fontSize = inputFont.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF444444),
                        unfocusedContainerColor = Color(0xFF444444),
                        focusedIndicatorColor = if (isRePasswordMatch || repassword.isEmpty()) Color.Transparent else Color.Red,
                        unfocusedIndicatorColor = if (isRePasswordMatch || repassword.isEmpty()) Color.Transparent else Color.Red,
                        cursorColor = green
                    ),
                    placeholder = { Text("Re-enter password", color = Color.LightGray, fontSize = inputFont.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    visualTransformation = if (rePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { rePasswordVisible = !rePasswordVisible }) {
                            Icon(
                                imageVector = if (rePasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )

                if (!isRePasswordMatch && repassword.isNotEmpty()) {
                    Text(
                        text = "Passwords do not match",
                        color = Color.Red,
                        fontSize = warningFont.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }


            // ---------------- SIGN UP BUTTON ----------------
            Spacer(Modifier.height(bigSpacer))

            Button(
                onClick = {
                    navController.navigate(AuthRoute.SignIn1)
                },
                enabled = isPasswordValid && isRePasswordMatch && username.isNotEmpty(),
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Sign up", fontSize = labelFont.sp)
            }
        }
    }
}
