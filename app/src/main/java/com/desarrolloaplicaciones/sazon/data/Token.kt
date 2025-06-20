package com.desarrolloaplicaciones.sazon.data

import android.content.Context
import com.desarrolloaplicaciones.sazon.App

object TokenManager {
    private const val PREF_NAME = "auth_preferences"
    private const val KEY_ACCESS_TOKEN = "access_token"

    private fun getSharedPreferences() =
        App.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(accessToken: String) {
        val prefs = getSharedPreferences()
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    fun getAccessToken(): String? {
        val prefs = getSharedPreferences()
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun removeToken() {
        val prefs = getSharedPreferences()
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()
    }
}