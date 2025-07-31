package com.example.haductrung.signup_login.LoginScreen


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel ( private val savedStateHandle: SavedStateHandle): ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()
    init {

        val savedUsername: String? = savedStateHandle.get("username")
        if (savedUsername != null) {
            _state.update { it.copy(username = savedUsername) }
            savedStateHandle.remove<String>("username")
        }


        val savedPassword: String? = savedStateHandle.get("password")
        if (savedPassword != null) {
            _state.update { it.copy(password = savedPassword) }
            savedStateHandle.remove<String>("password")
        }
    }
    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.onUsernameChange -> {
                _state.update { it.copy(username = intent.newUsername) }
            }

            is LoginIntent.onPasswordChange -> {
                _state.update { it.copy(password = intent.newPassword) }
            }

            is LoginIntent.onCheckedChange -> {
                _state.update { it.copy(isChecked = intent.newCheckedState) }
            }

            is LoginIntent.onTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is LoginIntent.onLoginClick -> {
                val currentState = _state.value
                if (currentState.username.isNotEmpty() && currentState.password.isNotEmpty()) {
                    viewModelScope.launch { _event.emit(LoginEvent.NavigateToHome) }
                }
            }

            is LoginIntent.SetNews -> {
                _state.update { LoginState() }
                viewModelScope.launch { _event.emit(LoginEvent.NavigateToSignUp) }
            }
        }
    }
}