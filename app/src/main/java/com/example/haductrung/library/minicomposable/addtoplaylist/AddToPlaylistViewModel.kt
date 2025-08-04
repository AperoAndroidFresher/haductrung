package com.example.haductrung.library.minicomposable.addtoplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.my_playlist.playlistdetail.PlaylistRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddToPlaylistViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository
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
                val playlists = playlistRepository.getPlaylists()
                _state.update { it.copy(isLoading = false, playlists = playlists) }
            }

            is AddToPlaylistIntent.OnPlaylistSelected -> {
                val songId = state.value.songIdToAdd
                if (songId != null && songId != -1) {
                    val currentSongIds = intent.playlist.songIds.toMutableList()
                    if (!currentSongIds.contains(songId)) {
                        currentSongIds.add(songId)
                        val updatedPlaylist = intent.playlist.copy(songIds = currentSongIds)
                        playlistRepository.updatePlaylist(updatedPlaylist)

                        viewModelScope.launch {
                            _event.emit(AddToPlaylistEvent.ShowSuccessMessage(updatedPlaylist.name))
                            _event.emit(AddToPlaylistEvent.GoBack)
                        }
                    } else {
                        viewModelScope.launch { _event.emit(AddToPlaylistEvent.GoBack) }
                    }
                }
            }

            is AddToPlaylistIntent.NavigateToMyPlaylistTab -> {
                viewModelScope.launch {
                    _event.emit(AddToPlaylistEvent.NavigateToMyPlaylist)
                }
            }
        }
    }
}