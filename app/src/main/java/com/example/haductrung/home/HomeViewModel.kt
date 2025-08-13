package com.example.haductrung.home


import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.MyApplication
import com.example.haductrung.home.remote.HomeApiClient
import com.example.haductrung.repository.UserRepository
import com.example.haductrung.signup_login.SessionManager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    application: MyApplication
) : AndroidViewModel(application) {
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
                    _state.value = HomeState()
                }
            }
            .launchIn(viewModelScope)
        fetchAllHomeData()
    }

    private fun fetchAllHomeData() {
        _state.update {
            it.copy(
                topAlbumsState = ContentLoadingState.Loading,
                topTracksState = ContentLoadingState.Loading,
                topArtistsState = ContentLoadingState.Loading
            )
        }

        viewModelScope.launch {
            fetchTopAlbums()
            fetchTopTracks()
            fetchTopArtists()
        }
    }

    private fun fetchTopAlbums() {
        viewModelScope.launch {

            _state.update { it.copy(topAlbumsState = ContentLoadingState.Loading) }
            try {
                // Gọi API
                val response = HomeApiClient.build().getTopAlbums()
                // Cập nhật state với dữ liệu thành công
                _state.update {
                    it.copy(
                        topAlbumsState = ContentLoadingState.Success,
                        topAlbums = response.topAlbums.albumList
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(topAlbumsState = ContentLoadingState.Error) }
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchTopTracks() {
        try {
            val response = HomeApiClient.build().getTopTracks()
            _state.update {
                it.copy(
                    topTracksState = ContentLoadingState.Success,
                    topTracks = response.topTracks.trackList
                )
            }
        } catch (e: Exception) {
            _state.update { it.copy(topTracksState = ContentLoadingState.Error) }
            e.printStackTrace()
        }
    }

    private suspend fun fetchTopArtists() {
        try {
            val response = HomeApiClient.build().getTopArtists()
            _state.update {
                it.copy(
                    topArtistsState = ContentLoadingState.Success,
                    topArtists = response.artists.artistList
                )
            }
        } catch (e: Exception) {
            _state.update { it.copy(topArtistsState = ContentLoadingState.Error) }
            e.printStackTrace()
        }
    }

    fun processIntent(intent: HomeIntent) {
        viewModelScope.launch {
            when (intent) {
                is HomeIntent.NavigateToProfile -> _event.emit(HomeEvent.NavigateToProfile)
                is HomeIntent.HomeTabClicked -> {}
                is HomeIntent.RetryFetchAll -> fetchAllHomeData()
                is HomeIntent.LibraryTabClicked -> _event.emit(HomeEvent.NavigateToLibrary)
                is HomeIntent.PlaylistTabClicked -> _event.emit(HomeEvent.NavigateToPlaylist)
                is HomeIntent.NavigateToTopAlbums -> _event.emit(HomeEvent.NavigateToTopAlbums)
                is HomeIntent.NavigateToTopArtists -> _event.emit(HomeEvent.NavigateToTopArtists)
                is HomeIntent.NavigateToTopTracks -> _event.emit(HomeEvent.NavigateToTopTracks)
            }
        }
    }
}