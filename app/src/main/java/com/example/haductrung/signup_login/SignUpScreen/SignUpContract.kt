package com.example.haductrung.signup_login.SignUpScreen

data class SignUpState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val emailError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

// INTENT
sealed interface SignUpIntent {
    data class onUsernameChange(val newUsername: String) : SignUpIntent
    data class onPasswordChange(val newPassword: String) : SignUpIntent
    data class onConfirmPasswordChange(val newConfirmPassword: String) : SignUpIntent
    data class onEmailChange(val newEmail: String) : SignUpIntent
    data object onSignUpClick : SignUpIntent
    data object onTogglePasswordVisibility : SignUpIntent
    data object onToggleConfirmPasswordVisibility : SignUpIntent

}

// EVENT
sealed interface SignUpEvent {
    data class NavigateBackToLogin(val username: String, val password: String) : SignUpEvent

}