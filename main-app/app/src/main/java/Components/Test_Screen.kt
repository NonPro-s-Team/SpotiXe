package Components

//
//import Components.Layout.OtpInputField
//import Components.Layout.SpotixeDialog
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.example.spotixe.Graph
//import com.example.spotixe.StartRoute
//import com.example.spotixe.auth.data.AuthDataStore
//import kotlinx.coroutines.launch
//
//@Composable
//fun DialogTestScreen(navController: NavHostController) {
//    var showTwoButtonsDialog by remember { mutableStateOf(false) }
//    var showSingleButtonDialog by remember { mutableStateOf(false) }
//    var showBanDialog by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val authDataStore = AuthDataStore(context)
//    val userData by authDataStore.getUserData().collectAsState(initial = null)
//    val otpValue = rememberSaveable { mutableStateOf("") }
//
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = "SpotiXe Dialog Test",
//                    style = MaterialTheme.typography.titleLarge
//                )
//
//                Button(
//                    onClick = { showTwoButtonsDialog = true },
//                    modifier = Modifier.fillMaxWidth(0.7f)
//                ) {
//                    Text("Mở dialog 2 nút (Confirm / Cancel)")
//                }
//
//                Button(
//                    onClick = { showSingleButtonDialog = true },
//                    modifier = Modifier.fillMaxWidth(0.7f)
//                ) {
//                    Text("Mở dialog 1 nút (OK)")
//                }
//
//                Button(
//                    onClick = { showBanDialog = true },
//                    modifier = Modifier.fillMaxWidth(0.7f)
//                ) {
//                    Text("Mày bị ban")
//                }
//
//                Spacer(Modifier.height(10.dp))
//
//                OtpInputField(
//                    otp = otpValue,
//                    count = 6,
//                    mask = true,
//                    onFilled = { code ->
//                        // Xử lý khi mã OTP được nhập đầy đủ
//                        println("Mã OTP đã nhập: $code")
//                    }
//                )
//            }
//
//            // Dialog 2 nút
//            SpotixeDialog(
//                visible = showTwoButtonsDialog,
//                title = "Xoá khỏi danh sách phát?",
//                message = "Bài hát này sẽ được gỡ khỏi queue hiện tại trong SpotiXe. Bạn có chắc chắn muốn tiếp tục?",
//                primaryButtonText = "Xoá",
//                secondaryButtonText = "Huỷ",
//                onPrimaryClick = {
//                    // TODO: xử lý logic xoá bài khỏi queue
//                    showTwoButtonsDialog = false
//                },
//                onSecondaryClick = {
//                    showTwoButtonsDialog = false
//                },
//                onDismissRequest = {
//                    showTwoButtonsDialog = false
//                }
//            )
//
//            // Dialog 1 nút
//            SpotixeDialog(
//                visible = showSingleButtonDialog,
//                title = "Đã thêm vào thư viện",
//                message = "Bài hát đã được thêm vào thư viện của bạn trong SpotiXe.",
//                primaryButtonText = "OK",
//                onPrimaryClick = {
//                    showSingleButtonDialog = false
//                },
//                onDismissRequest = {
//                    showSingleButtonDialog = false
//                }
//            )
//
//            SpotixeDialog(
//                visible = showBanDialog,
//                title = "Tài khoản mày đã bị ban",
//                message = "Tài khoản của mày đã vi phạm các điều khoản dịch vụ và bị ban (skibidi lên hình củacon chó cao bằng bộ pc). Cút khỏi app của tao.",
//                primaryButtonText = "Địt mẹ mày cook",
//                onPrimaryClick = {
//                    showBanDialog = false
//                    scope.launch {
//                        authDataStore.clearAll()
//                        navController.navigate(StartRoute.Start2) {
//                            popUpTo(Graph.MAIN) { inclusive = true }
//                        }
//                    }
//                },
//                onDismissRequest = {
//                },
//                dismissOnBackPress = false,
//                dismissOnClickOutside = false
//            )
//        }
//    }
//}


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import com.example.spotixe.services.MyFirebaseMessagingService
@AndroidEntryPoint
class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result ?: return@addOnCompleteListener
                Log.d("FCM_TOKEN", "Token: $token")

                // Nếu muốn show ra UI để copy luôn:
                // (ví dụ lưu vào một state/Datastore, hoặc tạm thời Toast)
                Toast.makeText(this, "FCM token logged in Logcat", Toast.LENGTH_SHORT).show()
            }


            MaterialTheme {
                val screen = intent.getStringExtra("screen") ?: "DefaultScreen"
                val messageId = intent.getStringExtra("messageId") ?: "NoMessageId"

                NotificationPermissionScreen(screen = screen, messageId = messageId)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val screen = intent.getStringExtra("screen") ?: "DefaultScreen"
        val messageId = intent.getStringExtra("messageId") ?: "NoMessageId"

        setContent {
            NotificationPermissionScreen(
                screen = screen,
                messageId = messageId
            )
        }
    }
}


@Composable
fun NotificationPermissionScreen(screen: String, messageId: String) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
    }

    // Check and launch permission request
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            permissionGranted = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔔 Screen: $screen",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "🧾 Message ID: $messageId",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (permissionGranted) {
            Text("✅ Notification permission granted")
        } else {
            Text("❗ Notification permission is required")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
                Text("Request Permission")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }) {
                Text("Open App Settings")
            }
        }
    }
}

