package com.example.land_parcel.Utils

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "user_session"
    private const val KEY_TOKEN = "session_token"
    private const val KEY_LOGIN_TIME = "login_time"

    fun saveSession(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_LOGIN_TIME, System.currentTimeMillis()) // save login time
            .apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getLoginTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LOGIN_TIME, 0)
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}


