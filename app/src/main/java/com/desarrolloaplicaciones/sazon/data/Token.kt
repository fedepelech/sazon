package com.desarrolloaplicaciones.sazon.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.desarrolloaplicaciones.sazon.App

object TokenManager {
    // Constantes
    private const val PREF_NAME = "sazon_secure_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"

    // Instancia única de SharedPreferences encriptadas
    private val encryptedPreferences: SharedPreferences by lazy {
        createEncryptedSharedPreferences()
    }

    /**
     * Crea una instancia de EncryptedSharedPreferences para almacenamiento seguro
     */
    private fun createEncryptedSharedPreferences(): SharedPreferences {
        return try {
            // Crear la clave maestra para el cifrado
            val masterKey = MasterKey.Builder(App.getAppContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            // Crear SharedPreferences encriptadas
            EncryptedSharedPreferences.create(
                App.getAppContext(),
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            App.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    // ============= MÉTODOS PARA CREDENCIALES =============

    /**
     * Guarda las credenciales del usuario de forma segura
     */
    fun saveCredentials(email: String, password: String) {
        encryptedPreferences.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    /**
     * Carga las credenciales guardadas
     */
    fun loadCredentials(): Pair<String, String> {
        val email = encryptedPreferences.getString(KEY_EMAIL, "") ?: ""
        val password = encryptedPreferences.getString(KEY_PASSWORD, "") ?: ""
        return email to password
    }

    /**
     * Elimina las credenciales guardadas
     */
    fun clearCredentials() {
        encryptedPreferences.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .apply()
    }

    /**
     * Verifica si hay credenciales guardadas
     */
    fun hasCredentials(): Boolean {
        val (email, password) = loadCredentials()
        return email.isNotEmpty() && password.isNotEmpty()
    }

    // ============= MÉTODOS PARA TOKEN =============

    /**
     * Guarda el token de acceso de forma segura
     */
    fun saveToken(accessToken: String) {
        encryptedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    /**
     * Obtiene el token de acceso guardado
     */
    fun getAccessToken(): String? {
        return encryptedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Elimina el token de acceso
     */
    fun removeToken() {
        encryptedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .apply()
    }

    /**
     * Verifica si hay un token válido guardado
     */
    fun hasValidToken(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    // ============= MÉTODOS PARA ESTADO DE SESIÓN =============

    /**
     * Marca al usuario como logueado
     */
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        encryptedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
    }

    /**
     * Verifica si el usuario está logueado
     */
    fun isUserLoggedIn(): Boolean {
        return encryptedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Guarda información adicional del usuario
     */
    fun saveUserInfo(userId: String, username: String) {
        encryptedPreferences.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    /**
     * Obtiene el ID del usuario
     */
    fun getUserId(): String? {
        return encryptedPreferences.getString(KEY_USER_ID, null)
    }

    /**
     * Obtiene el nombre de usuario
     */
    fun getUsername(): String? {
        return encryptedPreferences.getString(KEY_USERNAME, null)
    }

    /**
     * Guarda una sesión completa (token, credenciales, estado)
     */
    fun saveCompleteSession(
        email: String,
        password: String,
        accessToken: String,
    ) {
        encryptedPreferences.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    /**
     * Verifica si el usuario puede iniciar sesión automáticamente
     */
    fun canAutoLogin(): Boolean {
        return isUserLoggedIn() && hasValidToken() && hasCredentials()
    }

    /**
     * Limpia toda la información de sesión
     */
    fun clearAllSession() {
        encryptedPreferences.edit()
            .clear()
            .apply()
    }

    /**
     * Realiza logout completo
     */
    fun logout() {
        clearAllSession()
    }
}
