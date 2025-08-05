package com.example.haductrung.signup_login.loginScreen
//STATE
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isChecked: Boolean = true,
    val isPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null

)

// INTENT
sealed interface LoginIntent {
    data class OnUsernameChange(val newUsername: String) : LoginIntent
    data class OnPasswordChange(val newPassword: String) : LoginIntent
    data class OnCheckedChange(val newCheckedState: Boolean) : LoginIntent
    data object OnLoginClick : LoginIntent
    data object OnTogglePasswordVisibility : LoginIntent
    data object SetNews : LoginIntent
}

// EVENT
sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSignUp : LoginEvent
}