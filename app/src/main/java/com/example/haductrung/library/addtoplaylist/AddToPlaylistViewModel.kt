package com.example.haductrung.library.addtoplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.database.Converters
import com.example.haductrung.database.entity.PlaylistEntity
import com.example.haductrung.repository.PlaylistRepository
import com.example.haductrung.signup_login.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddToPlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddToPlaylistState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddToPlaylistEvent>()
    val event = _event.asSharedFlow()

    init {
        val songId: Int = savedStateHandle["songId"] ?: -1
        processIntent(AddToPlaylistIntent.LoadPlaylists(songId))
    }

    fun processIntent(intent: AddToPlaylistIntent) {
        when (intent) {
            is AddToPlaylistIntent.LoadPlaylists -> {
                _state.update { it.copy(isLoading = true, songIdToAdd = intent.songId) }
                val currentUserId = SessionManager.currentUserId.value
                if (currentUserId != null) {
                    viewModelScope.launch {
                        playlistRepository.getPlayListForUser(currentUserId).collect { playlists ->
                            _state.update { it.copy(isLoading = false, playlists = playlists) }
                        }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, playlists = emptyList()) }
                }
            }
            is AddToPlaylistIntent.OnPlaylistSelected -> {

                viewModelScope.launch(Dispatchers.IO) {
                    addSongToPlaylist(intent.playlist)
                }
            }

            is AddToPlaylistIntent.NavigateToMyPlaylistTab -> {
                viewModelScope.launch {
                    _event.emit(AddToPlaylistEvent.NavigateToMyPlaylist)
                }
            }
        }
    }

    private suspend fun addSongToPlaylist(selectedPlaylist: PlaylistEntity) {
        val songId = state.value.songIdToAdd
        if (songId != null && songId != -1) {
            val converters = Converters()
            val currentSongIds = converters.fromString(selectedPlaylist.songIdsJson).toMutableList()
            if (!currentSongIds.contains(songId)) {
                currentSongIds.add(songId)

                val updatedSongIdsJson = converters.fromIntList(currentSongIds)

                val updatedPlaylist = selectedPlaylist.copy(songIdsJson = updatedSongIdsJson)
                playlistRepository.updatePlaylist(updatedPlaylist)

                _event.emit(AddToPlaylistEvent.ShowSuccessMessage(updatedPlaylist.name))
            }
            _event.emit(AddToPlaylistEvent.GoBack)
        }
    }
}