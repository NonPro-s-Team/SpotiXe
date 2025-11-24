package com.example.spotixe.auth.data.models

import com.google.gson.annotations.SerializedName

data class VerifyOtpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("otp")
    val otp: String,

    @SerializedName("displayName")
    val username: String?
)

data class VerifyOtpRespone(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: VerifyResponeModel
)

