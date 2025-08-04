package com.example.haductrung.signup_login.SignUpScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.database.entity.UserEntity
import com.example.haductrung.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

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
                _state.update {
                    it.copy(
                        confirmPassword = intent.newConfirmPassword,
                        confirmPasswordError = null
                    )
                }
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
                viewModelScope.launch(Dispatchers.IO) {
                    validateAndSignUp()
                }
            }
        }
    }

    private suspend fun validateAndSignUp() {

        val currentState = _state.value
        var isValid = true
        _state.update {
            it.copy(
                usernameError = null,
                passwordError = null,
                confirmPasswordError = null,
                emailError = null
            )
        }
        if (currentState.username.isBlank() || !currentState.username.matches(Regex("^[a-zA-Z0-9]+$"))) {
            _state.update { it.copy(usernameError = "Invalid format") }
            isValid = false
        } else {
            val existingUser = userRepository.findUserByUsername(currentState.username)
            if (existingUser != null) {
                _state.update { it.copy(usernameError = " already exists") }
                isValid = false
            }
        }
        if (currentState.password.isBlank() || !currentState.password.matches(Regex("^[a-zA-Z0-9]+$"))) {
            _state.update { it.copy(passwordError = "Invalid format") }
            isValid = false
        }
        if (currentState.confirmPassword != currentState.password) {
            _state.update { it.copy(confirmPasswordError = " do not match") }
            isValid = false
        }
        if (currentState.email.isBlank() || !currentState.email.matches(Regex("^[a-zA-Z0-9._-]+@apero\\.vn$"))) {
            _state.update { it.copy(emailError = "Invalid email") }
            isValid = false
        }
        if (isValid) {
            val encoder = BCryptPasswordEncoder()
            val hashedPassword = encoder.encode(currentState.password)

            val newUser = UserEntity(
                username = currentState.username,
                passwordHash = hashedPassword,
                email = currentState.email
            )
            userRepository.createUser(newUser)


            _event.emit(
                SignUpEvent.NavigateBackToLogin(
                    currentState.username,
                    currentState.password
                )
            )
        }
    }
}