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
        observeUserSession()
    }

    fun processIntent(intent: PlaylistIntent) {
        when (intent) {
            is PlaylistIntent.OnToggleViewClick -> _state.update { it.copy(isGridView = !it.isGridView) }
            is PlaylistIntent.OnCreatePlaylistClick -> _state.update {
                it.copy(
                    showCreatePlaylistDialog = true
                )
            }

            is PlaylistIntent.OnDismissCreatePlaylistDialog -> _state.update {
                it.copy(
                    showCreatePlaylistDialog = false
                )
            }

            is PlaylistIntent.OnMoreClick -> _state.update { it.copy(playlistWithMenu = intent.playlist.playlistId.toString()) }
            is PlaylistIntent.OnDismissMenu -> _state.update { it.copy(playlistWithMenu = null) }
            is PlaylistIntent.OnRenamePlaylistClick -> _state.update {
                it.copy(
                    playlistToRename = intent.playlist,
                    playlistWithMenu = null
                )
            }

            is PlaylistIntent.OnDismissRenamePlaylistDialog -> _state.update {
                it.copy(
                    playlistToRename = null
                )
            }

            is PlaylistIntent.OnPlaylistClick -> navigateToDetail(intent.playlist)
            is PlaylistIntent.OnConfirmCreatePlaylist -> createNewPlaylist(intent.name)
            is PlaylistIntent.OnRemovePlaylist -> removePlaylist(intent.playlist)
            is PlaylistIntent.OnConfirmRenamePlaylist -> renamePlaylist(intent.newName)

            else -> {}
        }
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            SessionManager.currentUserId.collect { userId ->
                if (userId != null) {
                    playlistRepository.getPlayListForUser(userId).collect { playlistsFromDb ->
                        _state.update { it.copy(playlists = playlistsFromDb) }
                    }
                } else {
                    _state.update { it.copy(playlists = emptyList()) }
                }
            }
        }
    }

    private fun createNewPlaylist(name: String) {
        if (name.isBlank()) return
        val currentUserId = SessionManager.currentUserId.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val newPlaylist = PlaylistEntity(
                name = name,
                creatorUserId = currentUserId,
                songIdsJson = ""
            )
            playlistRepository.createPlaylist(newPlaylist)
            _state.update { it.copy(showCreatePlaylistDialog = false) }
        }
    }

    private fun removePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.deletePlaylist(playlist)
        }
    }

    private fun renamePlaylist(newName: String) {
        val playlistToRename = state.value.playlistToRename ?: return
        if (newName.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            val updatedPlaylist = playlistToRename.copy(name = newName)
            playlistRepository.updatePlaylist(updatedPlaylist)
            _state.update { it.copy(playlistToRename = null) }
        }
    }

    private fun navigateToDetail(playlist: PlaylistEntity) {
        viewModelScope.launch {
            _event.emit(PlaylistEvent.NavigateToPlaylistDetail(playlist.playlistId.toString()))
        }
    }
}
