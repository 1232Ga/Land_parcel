package com.example.land_parcel.Utils

import okhttp3.ResponseBody
import org.json.JSONObject

object Utils {

    fun parseErrorJson(errorJson: ResponseBody?): String {
        return try {
            val jsonObject = errorJson?.string()?.let { JSONObject(it) }

            var msg= jsonObject?.getString("message") ?: "Error"
            if (msg=="null" || msg=="Error"){
                msg= (jsonObject?.getJSONArray("errors")?.get(0)?:"Error").toString()
            }
            msg
        } catch (e: Exception) {
            e.message.toString()
        }

    }
}