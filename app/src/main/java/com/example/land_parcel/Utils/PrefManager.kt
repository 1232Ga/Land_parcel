package com.example.land_parcel.Utils

import android.content.Context
import javax.inject.Inject

class PrefManager @Inject constructor(context: Context) {
    private val sharedPreferences=context.getSharedPreferences("LandParcelPred",Context.MODE_PRIVATE)

    companion object{
        private val IS_LOGIN="isLogin"
        private const val CLIENT_ID="clientId"
        private const val TOKEN="token"
        private const val USER_NAME="userName"
        private const val USER_ID="userId"
        private const val GEO_JSON="geoJson"
        private const val VILLAGE_ID_NAME="villageIdName"

    }


    fun setLogin(login :Boolean){
        sharedPreferences.edit().putBoolean(IS_LOGIN,login).apply()
    }

    fun getLogin():Boolean{
        return sharedPreferences.getBoolean(IS_LOGIN,false)
    }

    fun setClientId(clientId:String){
        sharedPreferences.edit().putString(CLIENT_ID,clientId).apply()
    }
    fun getClientId():String?{
        return sharedPreferences.getString(CLIENT_ID,"")
    }
    fun setVillageIdName(villageIdName:String){
        sharedPreferences.edit().putString(VILLAGE_ID_NAME,villageIdName).apply()
    }
    fun getVillageIdName():String?{
        return sharedPreferences.getString(VILLAGE_ID_NAME,"")
    }
    fun setToken(token:String){
        sharedPreferences.edit().putString(TOKEN,token).apply()
    }

    fun getToken():String?{
        return sharedPreferences.getString(TOKEN,"")
    }

    fun setUserName(userName:String){
        sharedPreferences.edit().putString(USER_NAME,userName).apply()
    }

    fun getUserName():String?{
        return sharedPreferences.getString(USER_NAME,"")
    }

    fun setUserId(userId: String) {
        sharedPreferences.edit().putString(USER_ID, userId).apply()
    }
    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID, "")
    }

    fun setGeoJson(geoJson: String) {
        sharedPreferences.edit().putString(GEO_JSON, geoJson).apply()
    }

    fun getGeoJson(): String? {
        return sharedPreferences.getString(GEO_JSON, "{}")
    }

}