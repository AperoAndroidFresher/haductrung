package com.example.haductrung.signup_login.LoginScreen


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class LoginViewModel(
    private val savedStateHandle: SavedStateHandle ,
    private val userRepository: UserRepository
) : ViewModel() {

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
                viewModelScope.launch(Dispatchers.IO) {
                    loginUser()
                }
            }

            is LoginIntent.SetNews -> {
                _state.update { LoginState() }
                viewModelScope.launch { _event.emit(LoginEvent.NavigateToSignUp) }
            }
        }
    }

    private suspend fun loginUser() {
        val currentState = _state.value
        _state.update { it.copy(usernameError = null, passwordError = null) }
        val userFromDb = userRepository.findUserByUsername(currentState.username)
        if (userFromDb == null) {
            _state.update { it.copy(usernameError = "Username does not exist") }
        } else {
            val encoder = BCryptPasswordEncoder()
            if (encoder.matches(currentState.password, userFromDb.passwordHash)) {
                // correct pass
                _event.emit(LoginEvent.NavigateToHome)
            } else {
                // incorrect pass
                _state.update { it.copy(passwordError = "Incorrect password") }
            }
        }
    }
}