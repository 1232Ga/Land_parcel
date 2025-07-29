package com.example.land_parcel.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.land_parcel.model.login.LoginRequest
import com.example.land_parcel.repositories.LoginRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.provider.Settings
import com.example.land_parcel.Utils.PrefManager
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import android.util.Base64
import com.example.land_parcel.Utils.Constants.PUBLIC_KEY
import com.example.land_parcel.model.Logout.LogoutRequest
import com.example.land_parcel.repositories.LogoutRepositories

@HiltViewModel
class LogoutViewmodel @Inject constructor(private val logoutRepositories: LogoutRepositories, private val prefManager: PrefManager):ViewModel() {

    val logoutResponse=logoutRepositories.LogoutResponse
    suspend fun getLogout(userid: String) {
        val logoutRequest = LogoutRequest(userid)
        logoutRepositories.logout(logoutRequest)

    }

}