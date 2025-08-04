package com.example.haductrung.signup_login.LoginScreen
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
    data class onUsernameChange(val newUsername: String) : LoginIntent
    data class onPasswordChange(val newPassword: String) : LoginIntent
    data class onCheckedChange(val newCheckedState: Boolean) : LoginIntent
    data object onLoginClick : LoginIntent
    data object onTogglePasswordVisibility : LoginIntent
    data object SetNews : LoginIntent
}

// EVENT
sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSignUp : LoginEvent
}