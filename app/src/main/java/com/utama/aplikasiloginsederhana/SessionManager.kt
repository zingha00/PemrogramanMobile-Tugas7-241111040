package com.utama.aplikasiloginsederhana

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "EventAppSession"
        private const val KEY_IS_LOGIN = "isLogin"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "userId"
    }

    fun saveLoginSession(username: String, userId: Int = 0) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGIN, true)
            putString(KEY_USERNAME, username)
            putInt(KEY_USER_ID, userId)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "-") ?: "-"

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}