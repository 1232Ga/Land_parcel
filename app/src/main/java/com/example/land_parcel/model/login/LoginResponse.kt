package com.example.land_parcel.model.login

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String,
    @SerializedName("expiration") val expiration: String,
    @SerializedName("userId") val userId: String
)
