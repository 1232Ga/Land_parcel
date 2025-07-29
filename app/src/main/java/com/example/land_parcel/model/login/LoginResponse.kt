package com.example.land_parcel.model.login


data class LoginResponse(
    var message: String,
    var success: Boolean,
    var token: String,
)
