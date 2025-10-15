package com.example.land_parcel.model.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("userName") val userName: String,
    @SerializedName("password") val password: String,
    @SerializedName("logintype") val loginType: String
)