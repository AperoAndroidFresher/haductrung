package com.example.haductrung.signup_login.SignUpScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<SignUpEvent>()
    val event = _event.asSharedFlow()

    fun processIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.onUsernameChange -> {
                _state.update { it.copy(username = intent.newUsername, usernameError = null) }
            }
            is SignUpIntent.onPasswordChange -> {
                _state.update { it.copy(password = intent.newPassword, passwordError = null) }
            }
            is SignUpIntent.onConfirmPasswordChange -> {
                _state.update { it.copy(confirmPassword = intent.newConfirmPassword, confirmPasswordError = null) }
            }
            is SignUpIntent.onEmailChange -> {
                _state.update { it.copy(email = intent.newEmail, emailError = null) }
            }
            is SignUpIntent.onTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is SignUpIntent.onToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is SignUpIntent.onSignUpClick -> {
                validateAndSignUp()
            }
        }
    }

    private fun validateAndSignUp() {

        val currentState = _state.value
        _state.update { it.copy(usernameError = null, passwordError = null, confirmPasswordError = null, emailError = null) }

        var isValid = true
        var finalState = _state.value

        if (currentState.username.isBlank() || !currentState.username.matches(Regex("^[a-zA-Z0-9]+$"))) {
            finalState = finalState.copy(usernameError = "invalid format")
            isValid = false
        }
        if (currentState.password.isBlank() || !currentState.password.matches(Regex("^[a-zA-Z0-9]+$"))) {
            finalState = finalState.copy(passwordError = "invalid format")
            isValid = false
        }
        if (currentState.confirmPassword != currentState.password) {
            finalState = finalState.copy(confirmPasswordError = "invalid format")
            isValid = false
        }
        if (currentState.email.isBlank() || !currentState.email.matches(Regex("^[a-zA-Z0-9._-]+@apero\\.vn$"))) {
            finalState = finalState.copy(emailError = "invalid email")
            isValid = false
        }

        if (isValid) {
            viewModelScope.launch {     _event.emit(SignUpEvent.NavigateBackToLogin(state.value.username, state.value.password))
            }
        } else {
            _state.value = finalState
        }
    }
}