package com.example.haductrung.myplaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.myplaylist.playlistdetail.PlaylistRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<PlaylistEvent>()
    val event = _event.asSharedFlow()
// Cập nhật myplaylist
    init {
        val existingPlaylists = PlaylistRepository.getPlaylists()
        _state.update { it.copy(playlists = existingPlaylists) }
    }
    fun processIntent(intent: PlaylistIntent) {
        when (intent) {
            is PlaylistIntent.OnToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }

            is PlaylistIntent.OnCreatePlaylistClick -> {
                _state.update { it.copy(showCreatePlaylistDialog = true) }
            }

            is PlaylistIntent.OnDismissCreatePlaylistDialog -> {
                _state.update { it.copy(showCreatePlaylistDialog = false) }
            }

            is PlaylistIntent.OnConfirmCreatePlaylist -> {
                if (intent.name.isNotBlank()) {
                    val newPlaylist = Playlist(name = intent.name)
                    PlaylistRepository.createPlaylist(newPlaylist)

                    _state.update {
                        it.copy(
                            playlists = PlaylistRepository.getPlaylists(),
                            showCreatePlaylistDialog = false
                        )
                    }
                }
            }

            is PlaylistIntent.OnMoreClick -> {
                _state.update { it.copy(playlistWithMenu = intent.playlist.id) }
            }

            is PlaylistIntent.OnDismissMenu -> {
                _state.update { it.copy(playlistWithMenu = null) }
            }

            is PlaylistIntent.OnRemovePlaylist -> {
                PlaylistRepository.removePlaylist(intent.playlist.id)
                _state.update {
                    it.copy(
                        playlists = PlaylistRepository.getPlaylists(),
                        playlistWithMenu = null
                    )
                }
            }

            is PlaylistIntent.OnRenamePlaylistClick -> {
                _state.update {
                    it.copy(
                        playlistToRename = intent.playlist,
                        playlistWithMenu = null
                    )
                }
            }

            is PlaylistIntent.OnDismissRenamePlaylistDialog -> {
                _state.update { it.copy(playlistToRename = null) }
            }

            is PlaylistIntent.OnConfirmRenamePlaylist -> {
                _state.update { currentState ->
                    val playlistToRename = currentState.playlistToRename
                    if (playlistToRename != null && intent.newName.isNotBlank() && playlistToRename.name != intent.newName) {
                        val updatedList = currentState.playlists.map { playlist ->
                            if (playlist.id == playlistToRename.id) {
                                playlist.copy(name = intent.newName)
                            } else {
                                playlist
                            }
                        }
                        currentState.copy(
                            playlists = updatedList,
                            playlistToRename = null
                        )
                    } else {
                        currentState.copy(playlistToRename = null)
                    }
                }
            }
            is PlaylistIntent.OnPlaylistClick -> {
                viewModelScope.launch {
                    _event.emit(PlaylistEvent.NavigateToPlaylistDetail(intent.playlist.id))
                }
            }
            else -> {

            }
        }
    }
}