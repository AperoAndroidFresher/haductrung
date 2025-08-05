package com.example.haductrung.signup_login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object SessionManager {
    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    fun login(userId: Int) {
        _currentUserId.value = userId
    }

}