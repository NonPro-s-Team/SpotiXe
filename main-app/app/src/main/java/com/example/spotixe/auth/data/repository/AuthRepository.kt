package com.example.spotixe.auth.data.repository

import android.content.Context
import android.util.Log
import com.example.spotixe.auth.data.AuthDataStore
import com.example.spotixe.auth.data.api.AuthApiService
import com.example.spotixe.auth.data.api.RetrofitClient
import com.example.spotixe.auth.data.models.LoginResponse
import com.example.spotixe.auth.data.models.RequestOtpRequest
import com.example.spotixe.auth.data.models.RequestOtpRespone
import com.example.spotixe.auth.data.models.VerifyOtpRequest
import com.example.spotixe.auth.data.models.VerifyOtpRespone
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Repository handling authentication operations
 * Follows MVVM architecture pattern
 */
class AuthRepository(private val context: Context) {
    
    private val authDataStore = AuthDataStore(context)
    private val authApiService: AuthApiService = RetrofitClient.getAuthApiService(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    companion object {
        private const val TAG = "AuthRepository"
        // Replace with your actual Web Client ID from Firebase Console
        private const val WEB_CLIENT_ID = "814326272937-an4u5sk1ha7i2obmo6pmve43td63o1pq.apps.googleusercontent.com"
    }
    
    /**
     * Get GoogleSignInClient for initiating Google Sign-In
     */
    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        
        return GoogleSignIn.getClient(context, gso)
    }
    
    /**
     * Sign in with Google using GoogleSignInAccount
     * This method is called after user selects Google account
     * 
     * @param account GoogleSignInAccount from the sign-in intent
     * @return Result with FirebaseUser or error
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                Log.d(TAG, "Firebase sign-in successful: ${firebaseUser.email}")
                Result.success(firebaseUser)
            } else {
                Log.e(TAG, "Firebase sign-in failed: user is null")
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error signing in with Google", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get Firebase ID Token from current user
     * This token is used to authenticate with backend
     * 
     * @return Firebase ID token string
     */
    suspend fun getFirebaseIdToken(): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val tokenResult = user.getIdToken(true).await()
                val idToken = tokenResult.token
                
                if (idToken != null) {
                    Log.d(TAG, "Firebase ID token retrieved successfully")
                    Result.success(idToken)
                } else {
                    Log.e(TAG, "Firebase ID token is null")
                    Result.failure(Exception("Firebase ID token is null"))
                }
            } else {
                Log.e(TAG, "No Firebase user logged in")
                Result.failure(Exception("No Firebase user logged in"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Firebase ID token", e)
            Result.failure(e)
        }
    }
    
    /**
     * Login to backend with Firebase ID Token
     * Exchange Firebase token for SpotiXe JWT token
     * 
     * @param firebaseIdToken Firebase ID token
     * @return Result with LoginResponse or error
     */
    suspend fun loginToBackend(firebaseIdToken: String): Result<LoginResponse> {
        return try {
            val response = authApiService.login("Bearer $firebaseIdToken")
            
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                
                if (loginResponse.success) {
                    Log.d(TAG, "Backend login successful")
                    Result.success(loginResponse)
                } else {
                    Log.e(TAG, "Backend login failed: success=false")
                    Result.failure(Exception("Backend login failed"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Backend login failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Backend login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging in to backend", e)
            Result.failure(e)
        }
    }
    
    /**
     * Save JWT token and user data to DataStore
     */
    suspend fun saveAuthData(loginResponse: LoginResponse) {
        try {
            // Save JWT token
            authDataStore.saveJwtToken(loginResponse.token)
            
            // Save user data
            authDataStore.saveUser(
                userId = loginResponse.user.userId,
                email = loginResponse.user.email,
                username = loginResponse.user.username,
                avatarUrl = loginResponse.user.avatarUrl,
                firebaseUid = loginResponse.user.firebaseUid,
                phone = loginResponse.user.phone
            )
            
            Log.d(TAG, "Auth data saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving auth data", e)
            throw e
        }
    }
    
    /**
     * Complete Google login flow
     * This is the main method that combines all steps:
     * 1. Sign in with Google/Firebase
     * 2. Get Firebase ID token
     * 3. Login to backend
     * 4. Save JWT and user data
     * 
     * @param account GoogleSignInAccount from sign-in intent
     * @return Result with LoginResponse or error
     */
    suspend fun completeGoogleLogin(account: GoogleSignInAccount): Result<LoginResponse> {
        return try {
            // Step 1: Sign in with Firebase
            val firebaseResult = signInWithGoogle(account)
            if (firebaseResult.isFailure) {
                return Result.failure(firebaseResult.exceptionOrNull()!!)
            }
            
            // Step 2: Get Firebase ID token
            val tokenResult = getFirebaseIdToken()
            if (tokenResult.isFailure) {
                return Result.failure(tokenResult.exceptionOrNull()!!)
            }
            val firebaseIdToken = tokenResult.getOrNull()!!
            
            // Step 3: Login to backend
            val loginResult = loginToBackend(firebaseIdToken)
            if (loginResult.isFailure) {
                return Result.failure(loginResult.exceptionOrNull()!!)
            }
            val loginResponse = loginResult.getOrNull()!!
            
            // Step 4: Save auth data
            saveAuthData(loginResponse)
            
            Log.d(TAG, "Complete Google login successful")
            Result.success(loginResponse)
        } catch (e: Exception) {
            Log.e(TAG, "Error in complete Google login flow", e)
            Result.failure(e)
        }
    }

    /**
     * Request an OTP to be sent to the provided email address
     *
     * @param email user's email to receive OTP
     * @return Result containing RequestOtpResponse or error
     */
    suspend fun requestOtp(email: String): Result<RequestOtpRespone> {
        return try {
            val response = authApiService.requestOtp(RequestOtpRequest(email))

            if (response.isSuccessful && response.body() != null) {
                val otpResponse = response.body()!!
                Log.d(TAG, "OTP request successful for email: $email")
                Result.success(otpResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "OTP request failed: ${response.code()} - $errorBody")
                Result.failure(Exception("OTP request failed: ${response.code()}"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error requesting OTP", e)
            Result.failure(e)
        }
    }


    /**
     * Send an OTP & Email to verify the otp valid or no
     * @param email user's email to verify otp
     * @param otp the one-time password to verify
     * @return Result containing VerifyOtpRespone or error
     */
    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpRespone> {
        return try{
            val respone = authApiService.verifyOtp(VerifyOtpRequest(email, otp))

            if(respone.isSuccessful && respone.body() != null){
                val verifyOtpRespone = respone.body()!!
                Log.d(TAG, "Verify OTP successful for email: $email")
                Result.success(verifyOtpRespone)
            } else {
                val errorBody = respone.errorBody()?.string()
                Log.e(TAG, "Verify OTP failed: ${respone.code()} - $errorBody")
                Result.failure(Exception("Verify OTP failed: ${respone.code()}"))
            }
        }catch (e: Exception) {
            Log.e(TAG, "Error verifying OTP", e)
            Result.failure(e)
        }
    }

    /**
     * Get JWT token from DataStore
     */
    fun getJwtToken(): Flow<String?> {
        return authDataStore.getJwtToken()
    }
    
    /**
     * Get user data from DataStore
     */
    fun getUserData(): Flow<AuthDataStore.UserData?> {
        return authDataStore.getUserData()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return authDataStore.isLoggedIn()
    }
    
    /**
     * Get current Firebase user
     */
    fun getCurrentFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    /**
     * Logout user
     * - Sign out from Firebase
     * - Sign out from Google
     * - Clear DataStore
     */
    suspend fun logout() {
        try {
            // Sign out from Firebase
            firebaseAuth.signOut()
            
            // Sign out from Google
            getGoogleSignInClient().signOut().await()
            
            // Clear DataStore
            authDataStore.clearAll()
            
            Log.d(TAG, "Logout successful")
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            throw e
        }
    }
}
