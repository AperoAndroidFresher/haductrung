package com.example.haductrung.myplayList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyPlaylistViewModel : ViewModel() {

    private val _state = MutableStateFlow(MyPlaylistState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyPlaylistEvent>()
    val event = _event.asSharedFlow()

    fun processIntent(intent: MyPlaylistIntent) {
        when (intent) {
            is MyPlaylistIntent.CreatePlaylistClicked -> {
                val newPlaylist = Playlist(
                    id = (_state.value.playlists.maxOfOrNull { it.id } ?: 0) + 1,
                    name = intent.playlistName
                )
                _state.update {
                    it.copy(playlists = it.playlists + newPlaylist)
                }
            }
            is MyPlaylistIntent.PlaylistClicked -> {
                viewModelScope.launch {
                    _event.emit(MyPlaylistEvent.NavigateToPlaylistDetail(intent.playlistId))
                }
            }
            is MyPlaylistIntent.MoreOptionsClicked -> {
                _state.update { it.copy(playlistIdWithMenu = intent.playlistId) }
            }
            is MyPlaylistIntent.DismissMenu -> {
                _state.update { it.copy(playlistIdWithMenu = null) }
            }
            is MyPlaylistIntent.DeletePlaylistClicked -> {
                val newList = _state.value.playlists.toMutableList().apply {
                    removeIf { it.id == intent.playlistId }
                }
                _state.update { it.copy(playlists = newList, playlistIdWithMenu = null) }
            }
            is MyPlaylistIntent.AddSongToPlaylist -> {
                _state.update { currentState ->
                    val updatedPlaylists = currentState.playlists.map { playlist ->
                        if (playlist.id == intent.playlistId) {
                            // Tạo một bản sao của playlist và thêm bài hát mới
                            playlist.copy(songs = playlist.songs + intent.song)
                        } else {
                            playlist
                        }
                    }
                    currentState.copy(playlists = updatedPlaylists)
                }
            }
        }
    }
}