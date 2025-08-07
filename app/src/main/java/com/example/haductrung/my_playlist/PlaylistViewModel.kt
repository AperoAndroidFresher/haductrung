package com.example.haductrung.my_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class PlaylistViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<PlaylistEvent>()
    val event = _event.asSharedFlow()

    init {
        val currentUserId = SessionManager.currentUserId.value

        if (currentUserId != null) {
            viewModelScope.launch {
                playlistRepository.getPlayListForUser(currentUserId).collect { playlistsFromDb ->
                    _state.update { it.copy(playlists = playlistsFromDb) }
                }
            }
        }
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

            is PlaylistIntent.OnMoreClick -> {
                _state.update { it.copy(playlistWithMenu = intent.playlist.playlistId.toString()) }
            }

            is PlaylistIntent.OnDismissMenu -> {
                _state.update { it.copy(playlistWithMenu = null) }
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

            is PlaylistIntent.OnPlaylistClick -> {
                viewModelScope.launch {
                    _event.emit(PlaylistEvent.NavigateToPlaylistDetail(intent.playlist.playlistId.toString()))
                }
            }

            is PlaylistIntent.OnConfirmCreatePlaylist -> {
                if (intent.name.isNotBlank()) {

                    val currentUserId = SessionManager.currentUserId.value
                    if (currentUserId != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val newPlaylist = PlaylistEntity(
                                name = intent.name,
                                creatorUserId = currentUserId,
                                songIdsJson = ""
                            )
                            playlistRepository.createPlaylist(newPlaylist)
                        }
                    }
                    _state.update { it.copy(showCreatePlaylistDialog = false) }
                }
            }
            is PlaylistIntent.OnRemovePlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.deletePlaylist(intent.playlist)
                }
            }
            is PlaylistIntent.OnConfirmRenamePlaylist -> {
                val playlistToRename = state.value.playlistToRename
                if (playlistToRename != null && intent.newName.isNotBlank()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val updatedPlaylist = playlistToRename.copy(name = intent.newName)
                        playlistRepository.updatePlaylist(updatedPlaylist)
                    }
                }
                _state.update { it.copy(playlistToRename = null) }
            }

        }
    }
}