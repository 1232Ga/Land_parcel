package com.example.land_parcel.model.login

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("expiration") val expiration: String? = null,
    @SerializedName("userId") val userId: String? = null,

    @SerializedName("type") val type: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("status") val status: Int? = null,
    @SerializedName("traceId") val traceId: String? = null,
    @SerializedName("errors") val errors: Map<String, List<String>>? = null
)

