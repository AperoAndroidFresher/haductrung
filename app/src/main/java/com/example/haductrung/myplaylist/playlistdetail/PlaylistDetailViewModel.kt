package com.example.haductrung.myplaylist.playlistdetail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.library.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state.asStateFlow()

    init {
        // Lấy playlistId tải dữ liệu
        val playlistId: String? = savedStateHandle["playlistId"]
        if (playlistId != null) {
            processIntent(PlaylistDetailIntent.LoadPlaylistDetails(playlistId))
        }
    }

    fun processIntent(intent: PlaylistDetailIntent) {
        when (intent) {
            is PlaylistDetailIntent.LoadPlaylistDetails -> {
                _state.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    val foundPlaylist = playlistRepository.findPlaylistById(intent.playlistId)
                    if (foundPlaylist != null) {
                        val songList = libraryRepository.getSongsByIds(foundPlaylist.songIds)
                        _state.update {
                            it.copy(
                                playlist = foundPlaylist,
                                songs = songList,
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }


            is PlaylistDetailIntent.OnToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }

            is PlaylistDetailIntent.OnMoreClick -> {
                _state.update { it.copy(songWithMenu = intent.song.id) }
            }

            // đóng menu
            is PlaylistDetailIntent.OnDismissMenu -> {
                _state.update { it.copy(songWithMenu = null) }
            }

            // xóa bài hát khỏi playlist
            is PlaylistDetailIntent.OnDeleteSongFromPlaylist -> {
                viewModelScope.launch {
                    val currentPlaylist = state.value.playlist
                    val songToRemove = intent.song

                    if (currentPlaylist != null) {
                        val newSongIds = currentPlaylist.songIds.filterNot { it == songToRemove.id }

                        val updatedPlaylist = currentPlaylist.copy(songIds = newSongIds)

                        playlistRepository.updatePlaylist(updatedPlaylist)

                        _state.update {
                            it.copy(
                                songs = libraryRepository.getSongsByIds(newSongIds),
                                songWithMenu = null
                            )
                        }
                    }
                }
            }

            is PlaylistDetailIntent.OnToggleSortClick -> {

            }
        }
    }
}