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

@HiltViewModel
class LoginViewmodel @Inject constructor(private val loginRepositories: LoginRepositories,
                                         private val prefManager: PrefManager):ViewModel() {

    val loginResponse=loginRepositories.loginResponse


    private var deviceId: String = "123456789"
    var encodedPassword: String = ""
    var encodedUsername: String = ""

    @SuppressLint("HardwareIds")
    fun getClientId(context: Context) {
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
        if (deviceId != null || !deviceId.isEmpty()) {
            prefManager.setClientId(deviceId)
        } else {
            prefManager.setClientId("123456789")
        }
    }
    fun encryptData(user: String, password: String) {
        val encrypteduser: ByteArray?
        val encryptedpass: ByteArray?
        try {
            val publicBytes: ByteArray = Base64.decode(PUBLIC_KEY, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val pubKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            encrypteduser = cipher.doFinal(user.toByteArray())
            encryptedpass = cipher.doFinal(password.toByteArray())
            encodedUsername = Base64.encodeToString(encrypteduser, Base64.DEFAULT)
            encodedUsername = encodedUsername.replace("[\n\r]".toRegex(), "")
            encodedPassword = Base64.encodeToString(encryptedpass, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun getLogin(userName: String, password: String,logintype : String) {
        val loginRequest = LoginRequest(userName, password,logintype)
        loginRepositories.login(loginRequest)

    }

}