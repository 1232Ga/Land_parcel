package com.example.land_parcel.Utils

import com.example.land_parcel.model.login.LoginResponse
import com.google.gson.Gson
import okhttp3.ResponseBody

object UtilsLogin {
        fun parseErrorJson(errorBody: ResponseBody?): String {
            return try {
                if (errorBody == null) return "Unknown error occurred"

                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody.charStream(), LoginResponse::class.java)

                // Try to extract a meaningful message
                val message = buildString {
                    errorResponse.title?.let { append(it) }
                    if (errorResponse.errors != null) {
                        append("\n")
                        errorResponse.errors.forEach { (field, messages) ->
                            append("$field: ${messages.joinToString(", ")}\n")
                        }
                    }
                }

                message.ifEmpty { "Unknown error occurred" }

            } catch (e: Exception) {
                e.printStackTrace()
                "Something went wrong while parsing error."
            }
        }


}