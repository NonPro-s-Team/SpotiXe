package com.example.spotixe.Pages.Pages.SignUpPages

import Components.Buttons.BackButton
import Components.Buttons.GoogleSignInButtonFirebase
import Components.Buttons.TermsAndPolicyCheck
import Components.isValidPassword
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
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
import com.example.spotixe.Graph
import com.example.spotixe.R

@Composable
fun Sign_UpEmail2Screen(
    navController: NavController
){
    var green = Color(0xFF58BA47)
    var password by rememberSaveable { mutableStateOf("") }
    var repassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rePasswordVisible by rememberSaveable { mutableStateOf(false) }
    val isPasswordValid = isValidPassword(password)
    val isRePasswordMatch = repassword.isNotEmpty() && repassword == password

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

            Spacer(Modifier.height(10.dp))

            Text(
                "Create your account",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // Password label
            Text(
                text = "Password",
                color = green,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(8.dp))

            Column {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF444444),
                        unfocusedContainerColor = Color(0xFF444444),
                        focusedIndicatorColor = if (isPasswordValid) Color.Transparent else Color.Red,
                        unfocusedIndicatorColor = if (isPasswordValid) Color.Transparent else Color.Red,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = { Text("Enter password", color = Color.LightGray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible)
                                    "Ẩn mật khẩu" else "Hiện mật khẩu",
                                tint = Color.White
                            )
                        }
                    }
                )

                if (!isPasswordValid && password.isNotEmpty()) {
                    Text(
                        text = "Mật khẩu c 8 ký tự, có chữ hoa, chữ thường và số",
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }


            Spacer(Modifier.height(8.dp ))

            // RePassword label
            Text(
                text = "Confirm your password",
                color = green,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(8.dp))

            // TextField cho RePassword
            Column {
                TextField(
                    value = repassword,
                    onValueChange = { repassword = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF444444),
                        unfocusedContainerColor = Color(0xFF444444),
                        focusedIndicatorColor = if (isRePasswordMatch || repassword.isEmpty()) Color.Transparent else Color.Red,
                        unfocusedIndicatorColor = if (isRePasswordMatch || repassword.isEmpty()) Color.Transparent else Color.Red,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = { Text("Re-enter password", color = Color.LightGray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (rePasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { rePasswordVisible = !rePasswordVisible }) {
                            Icon(
                                imageVector = if (rePasswordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (rePasswordVisible)
                                    "Ẩn mật khẩu" else "Hiện mật khẩu",
                                tint = Color.White
                            )
                        }
                    }
                )

                if (!isRePasswordMatch && repassword.isNotEmpty()) {
                    Text(
                        text = "Mật khẩu nhập lại không khớp",
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }


            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate(AuthRoute.SignIn1) },
                enabled =
                    isPasswordValid && password == repassword,
                modifier = Modifier
                    .width(150.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Sign up")
            }


            Spacer(modifier = Modifier.height(20.dp))

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
                    // Successfully logged in with JWT saved
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { error ->
                    // Error handled with Toast in the button
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = green)) {append("Already have account ?\n")}
                    withStyle(style = SpanStyle(color = green)) { append("Click here to ") }
                    withStyle(style = SpanStyle(color = Color.White, fontStyle = FontStyle.Italic)) { append("sign up") }
                },
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { println("Navigate to Sign In") }
            )
        }

    }
}

