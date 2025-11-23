package Components

import Components.Layout.BanDialog
import Components.Layout.OtpInputField
import Components.Layout.SpotixeDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.spotixe.Graph
import com.example.spotixe.StartRoute
import com.example.spotixe.auth.data.AuthDataStore
import kotlinx.coroutines.launch

@Composable
fun DialogTestScreen(navController: NavHostController) {
    var showTwoButtonsDialog by remember { mutableStateOf(false) }
    var showSingleButtonDialog by remember { mutableStateOf(false) }
    var showBanDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authDataStore = AuthDataStore(context)
    val userData by authDataStore.getUserData().collectAsState(initial = null)
    val otpValue = rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SpotiXe Dialog Test",
                    style = MaterialTheme.typography.titleLarge
                )

                Button(
                    onClick = { showTwoButtonsDialog = true },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Mở dialog 2 nút (Confirm / Cancel)")
                }

                Button(
                    onClick = { showSingleButtonDialog = true },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Mở dialog 1 nút (OK)")
                }

                Button(
                    onClick = { showBanDialog = true },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Mày bị ban")
                }

                Spacer(Modifier.height(10.dp))

                OtpInputField(
                    otp = otpValue,
                    count = 6,
                    mask = true,
                    onFilled = { code ->
                        // Xử lý khi mã OTP được nhập đầy đủ
                        println("Mã OTP đã nhập: $code")
                    }
                )
            }

            // Dialog 2 nút
            SpotixeDialog(
                visible = showTwoButtonsDialog,
                title = "Xoá khỏi danh sách phát?",
                message = "Bài hát này sẽ được gỡ khỏi queue hiện tại trong SpotiXe. Bạn có chắc chắn muốn tiếp tục?",
                primaryButtonText = "Xoá",
                secondaryButtonText = "Huỷ",
                onPrimaryClick = {
                    // TODO: xử lý logic xoá bài khỏi queue
                    showTwoButtonsDialog = false
                },
                onSecondaryClick = {
                    showTwoButtonsDialog = false
                },
                onDismissRequest = {
                    showTwoButtonsDialog = false
                }
            )

            // Dialog 1 nút
            SpotixeDialog(
                visible = showSingleButtonDialog,
                title = "Đã thêm vào thư viện",
                message = "Bài hát đã được thêm vào thư viện của bạn trong SpotiXe.",
                primaryButtonText = "OK",
                onPrimaryClick = {
                    showSingleButtonDialog = false
                },
                onDismissRequest = {
                    showSingleButtonDialog = false
                }
            )

            SpotixeDialog(
                visible = showBanDialog,
                title = "Tài khoản mày đã bị ban",
                message = "Tài khoản của mày đã vi phạm các điều khoản dịch vụ và bị ban (skibidi lên hình củacon chó cao bằng bộ pc). Cút khỏi app của tao.",
                primaryButtonText = "Địt mẹ mày cook",
                onPrimaryClick = {
                    showBanDialog = false
                    scope.launch {
                        authDataStore.clearAll()
                        navController.navigate(StartRoute.Start2) {
                            popUpTo(Graph.MAIN) { inclusive = true }
                        }
                    }
                },
                onDismissRequest = {
                },
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        }
    }
}
