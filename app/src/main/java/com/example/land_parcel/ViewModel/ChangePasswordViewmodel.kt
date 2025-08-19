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
import com.example.land_parcel.model.ChangePassword.ChangePasswordRequest
import com.example.land_parcel.model.Logout.LogoutRequest
import com.example.land_parcel.repositories.ChangePasswordRepositories
import com.example.land_parcel.repositories.LogoutRepositories

@HiltViewModel
class ChangePasswordViewmodel @Inject constructor(private val changepasswordRepo: ChangePasswordRepositories, private val prefManager: PrefManager):ViewModel() {

    val changeResponse=changepasswordRepo.changepassResponse

    suspend fun ChangePassword(token:String,userID:String,oldPassword: String, newPassword: String) {
        val changePasswordRequest = ChangePasswordRequest(oldPassword,newPassword)
        changepasswordRepo.changepassword(token,userID,changePasswordRequest)
    }

}