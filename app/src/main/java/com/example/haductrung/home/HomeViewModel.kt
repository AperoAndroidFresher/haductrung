package com.example.haductrung.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

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