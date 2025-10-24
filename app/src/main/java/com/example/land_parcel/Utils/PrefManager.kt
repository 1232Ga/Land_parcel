package com.example.land_parcel.Utils

import android.content.Context
import com.example.land_parcel.model.VillageModel.VillageItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class PrefManager @Inject constructor(context: Context) {
    private val sharedPreferences=context.getSharedPreferences("LandParcelPred",Context.MODE_PRIVATE)
    private val gson = Gson()
    companion object{
        private val IS_LOGIN="isLogin"
        private const val CLIENT_ID="clientId"
        private const val TOKEN="token"
        private const val USER_NAME="userName"
        private const val USER_ID="userId"
        private const val GEO_JSON="geoJson"
        private const val PNIL_JSON="pnil"
        private const val VILLAGEIDHYPEN="villageIdhypen"
        private const val VILLAGEIDWithoutHYPEN="villageIdwithouthypen"
        private const val REPORTINIATE="reportiniate"

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



    fun getVillageIDHypen(): String? {
        return sharedPreferences.getString(VILLAGEIDHYPEN, "")
    }
    fun setVillageIDHypen(VillageIDHypen: String?) {
        sharedPreferences.edit().putString(VILLAGEIDHYPEN, VillageIDHypen).apply()
    }
    fun getVillageIDWithoutHypen(): String? {
        return sharedPreferences.getString(VILLAGEIDWithoutHYPEN, "")
    }
    fun setVillageIDWithoutHypen(VillageIDWithoutHypen: String?) {
        sharedPreferences.edit().putString(VILLAGEIDWithoutHYPEN, VillageIDWithoutHypen).apply()
    }

    fun setPnil(pnil: String?) {
        sharedPreferences.edit().putString(PNIL_JSON, pnil).apply()
    }
    fun getPnil(): String? {
        return sharedPreferences.getString(PNIL_JSON, "")
    }

    fun setreportinitaite(reportintiate: String?) {
        sharedPreferences.edit().putString(REPORTINIATE, reportintiate).apply()
    }

    fun getreportintiate(): String? {
        return sharedPreferences.getString(REPORTINIATE, "Not Generate")
    }
    fun setVillageList(villageList: List<VillageItem>) {
        val json = gson.toJson(villageList)
        sharedPreferences.edit().putString("village_list", json).apply()
    }

    fun getVillageList(): List<VillageItem>? {
        val json = sharedPreferences.getString("village_list", null)
        return if (json.isNullOrEmpty()) null
        else gson.fromJson(json, object : TypeToken<List<VillageItem>>() {}.type)
    }
}