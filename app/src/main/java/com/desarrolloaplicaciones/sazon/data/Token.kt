package com.desarrolloaplicaciones.sazon.data

import android.content.Context
import com.desarrolloaplicaciones.sazon.App
import android.util.Base64
import org.json.JSONObject

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

    fun getUserId(): String? {
        val token = getAccessToken() ?: return null
        val parts = token.split(".")
        if (parts.size != 3) return null

        return try {
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val payload = JSONObject(payloadJson)
            payload.optString("userId", null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}