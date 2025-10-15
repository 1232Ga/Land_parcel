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
import com.google.gson.Gson

@HiltViewModel
class ChangePasswordViewmodel @Inject constructor(private val changepasswordRepo: ChangePasswordRepositories, private val prefManager: PrefManager):ViewModel() {
    val changeResponse=changepasswordRepo.changepassResponse
    var encodedoldPassword: String = ""
    var encodednewPassword: String = ""

    suspend fun ChangePassword(token:String,userID:String,oldPassword: String, newPassword: String) {
        val changePasswordRequest = ChangePasswordRequest(oldPassword,newPassword)
        val gson = Gson()
        val requestJson = gson.toJson(changePasswordRequest)
        changepasswordRepo.changepassword(token,userID,changePasswordRequest)
    }

    fun encryptData(oldPassword: String, newpassword: String) {
        val encryptedold: ByteArray?
        val encryptednew: ByteArray?
        try {
            val publicBytes: ByteArray = Base64.decode(PUBLIC_KEY, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val pubKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            encryptedold = cipher.doFinal(oldPassword.toByteArray())
            encryptednew = cipher.doFinal(newpassword.toByteArray())
            encodedoldPassword = Base64.encodeToString(encryptedold, Base64.DEFAULT)
            encodedoldPassword = encodedoldPassword.replace("[\n\r]".toRegex(), "")
            encodednewPassword = Base64.encodeToString(encryptednew, Base64.DEFAULT)
            encodednewPassword = encodednewPassword.replace("[\n\r]".toRegex(), "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}