package com.example.haductrung.signup_login

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object SessionManager {
    private const val PREFS_NAME = "AppSessionPrefs"
    private const val KEY_USER_ID = "logged_in_user_id"
    private var sharedPreferences: SharedPreferences? = null
    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId = _currentUserId.asStateFlow()
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _currentUserId.value = getLoggedInUserId()
    }
    fun login(userId: Int, remember: Boolean) {
        _currentUserId.value = userId
        if (remember) {
            sharedPreferences?.edit {
                putInt(KEY_USER_ID, userId)
            }
        }
    }
    fun logout() {
        _currentUserId.value = null
        sharedPreferences?.edit {
            remove(KEY_USER_ID)
        }
    }
     fun getLoggedInUserId(): Int? {
        val userId = sharedPreferences?.getInt(KEY_USER_ID, -1) ?: -1
        return if (userId != -1) userId else null
    }

}