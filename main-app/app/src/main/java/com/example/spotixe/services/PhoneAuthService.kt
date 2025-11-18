package com.example.spotixe.services

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

// Lưu tạm thông tin giữa màn 1 và 2
object PhoneAuthSession {
    var phoneNumber: String? = null
    var verificationId: String? = null
}

fun startPhoneVerification(
    activity: Activity,
    rawPhone: String,
    onCodeSent: () -> Unit,
    onError: (String) -> Unit
) {
    val phone = rawPhone.trim()

    if (phone.isBlank()) {
        onError("Vui lòng nhập số điện thoại")
        return
    }

    val auth = Firebase.auth

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto verify (Google tự đọc SMS). Ở đây không sign in luôn
            // để giữ flow 3 màn hình => vẫn để user nhập OTP rồi verify ở màn 2.
        }

        override fun onVerificationFailed(e: FirebaseException) {
            onError(e.localizedMessage ?: "Gửi OTP thất bại")
        }

        override fun onCodeSent(verificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken
        ) {
            // Lưu lại để dùng ở màn 2
            PhoneAuthSession.phoneNumber = phone
            PhoneAuthSession.verificationId = verificationId
            onCodeSent()
        }
    }

    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phone)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}

fun verifyOtpCode(
    activity: Activity,
    code: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val verId = PhoneAuthSession.verificationId
    if (verId.isNullOrEmpty()) {
        onError("Không tìm thấy session OTP, vui lòng gửi lại mã.")
        return
    }

    val auth = Firebase.auth
    val credential = PhoneAuthProvider.getCredential(verId, code)

    auth.signInWithCredential(credential)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError(task.exception?.localizedMessage ?: "Xác thực thất bại")
            }
        }
}

fun normalizeVietnamPhone(input: String): String {
    val raw = input.trim().replace(" ", "")

    return when {
        raw.startsWith("+84") -> raw                    // đã đúng chuẩn
        raw.startsWith("84") -> "+$raw"                // thiếu dấu +
        raw.startsWith("0") -> "+84" + raw.drop(1)    // 0xxxxxxxxx -> +84xxxxxxxxx
        else -> raw                    // trường hợp khác giữ nguyên
    }
}
