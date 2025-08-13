package com.example.haductrung.home


import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.MyApplication
import com.example.haductrung.repository.UserRepository
import com.example.haductrung.signup_login.SessionManager
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel (
    private val userRepository: UserRepository,
    application: MyApplication
): AndroidViewModel(application) {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()
    init {
        SessionManager.currentUserId
            .onEach { userId ->
                if (userId != null) {
                    userRepository.findUserById(userId)?.collect { userEntity ->
                        _state.update { currentState ->
                            currentState.copy(
                                username = userEntity?.username ?: "",
                                imageUri = userEntity?.imageUri?.toUri()
                            )
                        }
                    }
                } else {
                    // Nếu user đăng xuất, reset state
                    _state.value = HomeState()
                }
            }
            .launchIn(viewModelScope) // Chạy coroutine
    }

    fun processIntent(intent: HomeIntent) {
        viewModelScope.launch {
            when (intent) {
                is HomeIntent.NavigateToProfile -> _event.emit(HomeEvent.NavigateToProfile)
                is HomeIntent.HomeTabClicked -> {  }
                is HomeIntent.LibraryTabClicked -> _event.emit(HomeEvent.NavigateToLibrary)
                is HomeIntent.PlaylistTabClicked -> _event.emit(HomeEvent.NavigateToPlaylist)
            }
        }
    }
}