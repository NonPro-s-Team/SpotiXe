package com.example.spotixe.auth.data.models

import com.google.gson.annotations.SerializedName

data class RequestOtpRequest(
    @SerializedName("email")
    val email: String
)

data class RequestOtpRespone(
    @SerializedName("message")
    val message: String
)
