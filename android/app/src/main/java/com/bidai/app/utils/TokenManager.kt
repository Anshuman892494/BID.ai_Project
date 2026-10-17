package com.bidai.app.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "bidai_auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_CREDITS = "user_credits"
        private const val KEY_USER_ECO = "user_eco"

        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveAuthData(token: String, id: String, name: String, email: String, credits: Int = 100, eco: Int = 0) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putInt(KEY_USER_CREDITS, credits)
            putInt(KEY_USER_ECO, eco)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "User") ?: "User"

    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun getCredits(): Int = prefs.getInt(KEY_USER_CREDITS, 100)

    fun getEcoScore(): Int = prefs.getInt(KEY_USER_ECO, 50)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clearAuth() {
        prefs.edit().clear().apply()
    }
}
