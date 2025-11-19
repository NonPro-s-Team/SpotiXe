package com.example.spotixe.Pages.Pages.AppMainPages

import Components.Buttons.BackButton
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spotixe.R
import com.example.spotixe.auth.data.AuthDataStore
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authDataStore = AuthDataStore(context)
    val userData by authDataStore.getUserData().collectAsState(initial = null)

    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf(LocalDate.of(2000, 1, 1)) }
    var joinDate by remember { mutableStateOf(LocalDate.now()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    var editName by remember { mutableStateOf(false) }
    var showDobPicker by remember { mutableStateOf(false) }
    var showJoinPicker by remember { mutableStateOf(false) }

    // Load user data when available
    LaunchedEffect(userData) {
        userData?.let { user ->
            name = user.username ?: ""
            email = user.email ?: ""
            phone = user.phone ?: ""
        }
    }

    val dobMillis: Long = remember(dob) {
        dob.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    val joinMillis: Long = remember(joinDate) {
        joinDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    val dobState = rememberDatePickerState(
        initialSelectedDateMillis = dobMillis
    )
    val joinState = rememberDatePickerState(
        initialSelectedDateMillis = joinMillis
    )

    Scaffold(
        containerColor = Color(0xFF121212),
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF121212))
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(navController = navController)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Hồ sơ người dùng",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF58BA47)
                )
            }

            // Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (userData?.avatarUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(userData?.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF58BA47)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Avatar",
                            modifier = Modifier.size(100.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            // Name Field
            EditableField(
                label = "Tên",
                value = name,
                onValueChange = { name = it },
                isEditing = editName,
                onEditToggle = { editName = !editName }
            )

            Spacer(Modifier.height(16.dp))

            // Email Field (disabled - from Firebase)
            EditableField(
                label = "Email",
                value = email,
                onValueChange = { },
                isEditing = false,
                onEditToggle = { },
                enabled = false
            )

            Spacer(Modifier.height(16.dp))

            // Join Date
            EditableField(
                label = "Ngày tham gia",
                value = joinDate.toString(),
                onValueChange = { },
                isEditing = false,
                onEditToggle = { },
                enabled = false
            )

            Spacer(Modifier.height(24.dp))

            // Error message
            if (errorMsg.isNotEmpty()) {
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 50.dp, vertical = 8.dp)
                )
            }

            // Save Button
            Button(
                onClick = {
                    errorMsg = ""
                    if (name.isBlank()) {
                        errorMsg = "Tên không được để trống."
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            authDataStore.saveUser(
                                userId = userData?.userId ?: 0L,
                                email = email,
                                username = name,
                                avatarUrl = userData?.avatarUrl,
                                firebaseUid = userData?.firebaseUid ?: "",
                                phone = phone
                            )
                            Toast.makeText(context, "Đã lưu thành công!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMsg = "Lỗi khi lưu: ${e.message}"
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp, vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58BA47)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Lưu", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        // Date Pickers
        if (showDobPicker) {
            DatePickerDialog(
                onDismissRequest = { showDobPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dobState.selectedDateMillis?.let {
                            dob = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDobPicker = false
                    }) { Text("OK", color = Color(0xFF58BA47)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDobPicker = false }) {
                        Text("Hủy", color = Color.White)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = Color(0xFF1E1E1E))
            ) {
                DatePicker(state = dobState)
            }
        }

        if (showJoinPicker) {
            DatePickerDialog(
                onDismissRequest = { showJoinPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        joinState.selectedDateMillis?.let {
                            joinDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showJoinPicker = false
                    }) { Text("OK", color = Color(0xFF58BA47)) }
                },
                dismissButton = {
                    TextButton(onClick = { showJoinPicker = false }) {
                        Text("Hủy", color = Color.White)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = Color(0xFF1E1E1E))
            ) {
                DatePicker(state = joinState)
            }
        }
    }
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            enabled = enabled && isEditing,
            readOnly = !isEditing,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(0.7f),
                disabledTextColor = Color.White.copy(0.5f),
                focusedBorderColor = Color(0xFF58BA47),
                unfocusedBorderColor = Color.White.copy(0.5f),
                disabledBorderColor = Color.White.copy(0.3f),
                focusedLabelColor = Color(0xFF58BA47),
                unfocusedLabelColor = Color.White.copy(0.5f),
                disabledLabelColor = Color.White.copy(0.3f)
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType
            )
        )

        if (enabled) {
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onEditToggle) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = if (isEditing) Color(0xFF58BA47) else Color.White.copy(0.7f)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateField(
    label: String,
    date: LocalDate,
    formatter: DateTimeFormatter,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = date.format(formatter),
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(0.7f),
                disabledTextColor = Color.White.copy(0.5f),
                focusedBorderColor = Color(0xFF58BA47),
                unfocusedBorderColor = Color.White.copy(0.5f),
                focusedLabelColor = Color(0xFF58BA47),
                unfocusedLabelColor = Color.White.copy(0.5f)
            )
        )

        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit Date",
                tint = Color.White.copy(0.7f)
            )
        }
    }
}
