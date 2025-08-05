package com.example.haductrung.signup_login.signUpScreen

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
    data class OnUsernameChange(val newUsername: String) : SignUpIntent
    data class OnPasswordChange(val newPassword: String) : SignUpIntent
    data class OnConfirmPasswordChange(val newCOnfirmPassword: String) : SignUpIntent
    data class OnEmailChange(val newEmail: String) : SignUpIntent
    data object OnSignUpClick : SignUpIntent
    data object OnTogglePasswordVisibility : SignUpIntent
    data object OnToggleConfirmPasswordVisibility : SignUpIntent

}

// EVENT
sealed interface SignUpEvent {
    data class NavigateBackToLogin(val username: String, val password: String) : SignUpEvent

}