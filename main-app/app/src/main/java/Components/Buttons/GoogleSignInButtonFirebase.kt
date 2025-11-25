package Components.Buttons

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.spotixe.R
import com.example.spotixe.auth.data.AuthDataStore
import com.example.spotixe.auth.data.api.RetrofitClient
import com.example.spotixe.auth.data.api.AuthApiService
import com.example.spotixe.auth.data.models.LoginResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun GoogleSignInButtonFirebase(
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFE7ECF5),
    cornerRadius: Int = 12,
    onSuccess: (LoginResponse) -> Unit,
    onError: (errorMessage: String, errorCode: String?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    val authDataStore = remember { AuthDataStore(context) }
    val authApiService = remember { RetrofitClient.getAuthApiService(context) }
    var loading by remember { mutableStateOf(false) }

    // Build GoogleSignInClient with default_web_client_id
    val webClientId = stringResource(id = R.string.default_web_client_id)
    val gso = remember(webClientId) {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    val googleClient = remember { GoogleSignIn.getClient(activity, gso) }

    // Launcher for the Google sign-in Intent
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "Result received: ${result.resultCode}")
        
        if (result.resultCode != Activity.RESULT_OK) {
            loading = false
            Log.e("GoogleSignIn", "Sign-in cancelled or failed")
            Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
            onError("Sign-in cancelled", null)
            return@rememberLauncherForActivityResult
        }
        
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "Google account obtained: ${account.email}")
            
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            
            // Step 1: Sign in with Firebase
            Log.d("GoogleSignIn", "Step 1: Signing in with Firebase...")
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { signInTask ->
                    if (signInTask.isSuccessful) {
                        Log.d("GoogleSignIn", "Firebase sign-in successful")
                        val firebaseUser = auth.currentUser
                        
                        if (firebaseUser != null) {
                            Log.d("GoogleSignIn", "Firebase user: ${firebaseUser.email}")
                            
                            // Step 2: Get Firebase ID Token and call backend
                            scope.launch {
                                try {
                                    Log.d("GoogleSignIn", "Step 2: Getting Firebase ID token...")
                                    val tokenResult = firebaseUser.getIdToken(true).await()
                                    val firebaseIdToken = tokenResult.token
                                    
                                    if (firebaseIdToken != null) {
                                        Log.d("GoogleSignIn", "Firebase ID token obtained: ${firebaseIdToken.take(20)}...")
                                        
                                        // Step 3: Call backend API to exchange for JWT
                                        Log.d("GoogleSignIn", "Step 3: Calling backend API...")
                                        val response = authApiService.login("Bearer $firebaseIdToken")
                                        
                                        Log.d("GoogleSignIn", "Backend response code: ${response.code()}")
                                        
                                        if (response.isSuccessful && response.body() != null) {
                                            val loginResponse = response.body()!!
                                            Log.d("GoogleSignIn", "Backend response success: ${loginResponse.success}")
                                            
                                            if (loginResponse.success) {
                                                Log.d("GoogleSignIn", "Step 4: Saving auth data...")
                                                
                                                // Step 4: Save JWT token and user data
                                                authDataStore.saveJwtToken(loginResponse.token)
                                                authDataStore.saveUser(
                                                    userId = loginResponse.user.userId,
                                                    email = loginResponse.user.email,
                                                    username = loginResponse.user.username,
                                                    avatarUrl = loginResponse.user.avatarUrl,
                                                    firebaseUid = loginResponse.user.firebaseUid,
                                                    phone = loginResponse.user.phone
                                                )
                                                
                                                Log.d("GoogleSignIn", "Auth data saved successfully")
                                                loading = false
                                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                                Log.d("GoogleSignIn", "Calling onSuccess callback...")
                                                onSuccess(loginResponse)
                                            } else {
                                                loading = false
                                                Log.e("GoogleSignIn", "Backend login failed: success=false")
                                                onError("Backend login failed", null)
                                            }
                                        } else {
                                            loading = false
                                            val errorMsg = response.errorBody()?.string() ?: "Backend error: ${response.code()}"
                                            Log.e("GoogleSignIn", "Backend API error: $errorMsg")
                                            onError(errorMsg, response.code().toString())
                                        }
                                    } else {
                                        loading = false
                                        Log.e("GoogleSignIn", "Firebase ID token is null")
                                        onError("Firebase token error", null)
                                    }
                                } catch (e: Exception) {
                                    loading = false
                                    Log.e("GoogleSignIn", "Exception in coroutine: ${e.message}", e)
                                    onError(e.message ?: "Unknown error occurred", null)
                                }
                            }
                        } else {
                            loading = false
                            Log.e("GoogleSignIn", "Firebase user is null after sign-in")
                            onError("Firebase user error", null)
                        }
                    } else {
                        loading = false
                        val exception = signInTask.exception
                        Log.e("GoogleSignIn", "Firebase sign-in failed: ${exception?.message}", exception)

                        // Xác định loại lỗi từ Firebase
                        val errorMessage = when {
                            exception?.message?.contains("user account has been disabled", ignoreCase = true) == true ->
                                "Your account has been disabled by an administrator. Please contact support."
                            exception?.message?.contains("disabled", ignoreCase = true) == true ->
                                "This account has been disabled. Please contact support for assistance."
                            exception?.message?.contains("network", ignoreCase = true) == true ->
                                "Network error. Please check your internet connection and try again."
                            else -> exception?.message ?: "Firebase sign-in failed"
                        }

                        onError(errorMessage, null)
                    }
                }
        } catch (e: ApiException) {
            loading = false
            Log.e("GoogleSignIn", "Google Sign-In API exception: ${e.statusCode} - ${e.message}", e)
            onError("Google Sign-In failed: ${e.message}", e.statusCode.toString())
        } catch (e: Exception) {
            loading = false
            Log.e("GoogleSignIn", "Unexpected exception: ${e.message}", e)
            onError(e.message ?: "Unexpected error occurred", null)
        }
    }

    // UI Button
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .clickable(enabled = !loading) {
                loading = true
                // Sign out from Google to force account picker
                scope.launch {
                    try {
                        googleClient.signOut().await()
                        Log.d("GoogleSignIn", "Google client signed out, launching account picker")
                    } catch (e: Exception) {
                        Log.w("GoogleSignIn", "Error signing out: ${e.message}")
                    }
                    launcher.launch(googleClient.signInIntent)
                }
            },
        color = containerColor,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(containerColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .defaultMinSize(minHeight = 44.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Signing in...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

/** Optional helpers for reuse elsewhere */
object GoogleAuthUtils {
    fun signOut(onComplete: (Boolean) -> Unit = {}) {
        FirebaseAuth.getInstance().signOut()
        onComplete(true)
    }
}