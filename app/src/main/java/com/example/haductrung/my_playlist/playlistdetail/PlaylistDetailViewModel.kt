package com.example.haductrung.my_playlist.playlistdetail


import android.annotation.SuppressLint
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.repository.SongRepository
import com.example.haductrung.database.Converters
import com.example.haductrung.database.entity.SongEntity
import com.example.haductrung.repository.Song
import com.example.haductrung.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlaylistDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state.asStateFlow()

    init {
        val playlistId: String? = savedStateHandle["playlistId"]
        if (playlistId != null) {
            processIntent(PlaylistDetailIntent.LoadPlaylistDetails(playlistId))
        }
    }

    fun processIntent(intent: PlaylistDetailIntent) {
        when (intent) {
            is PlaylistDetailIntent.LoadPlaylistDetails -> {
                loadPlaylistAndSongs(intent.playlistId)
            }
            is PlaylistDetailIntent.OnToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }
            is PlaylistDetailIntent.OnMoreClick -> {
                _state.update { it.copy(songWithMenu = intent.song.id) }
            }
            is PlaylistDetailIntent.OnDismissMenu -> {
                _state.update { it.copy(songWithMenu = null) }
            }
            is PlaylistDetailIntent.OnDeleteSongFromPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    removeSongFromPlaylist(intent.song)
                }
            }
            else -> {}
        }
    }

    private fun loadPlaylistAndSongs(playlistId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val playlistIdInt = playlistId.toIntOrNull() ?: 0

            // Lấy thông tin playlist
            val playlistEntity = playlistRepository.findPlaylistById(playlistIdInt)
            if (playlistEntity != null) {
                _state.update { it.copy(playlist = playlistEntity) }

                // Dùng converter để lấy danh sách ID
                val songIds = Converters().fromString(playlistEntity.songIdsJson)
                songRepository.getSongsByIds(songIds).collect { songEntities ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            songs = mapEntitiesToSongs(songEntities)
                        )
                    }
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun removeSongFromPlaylist(songToRemove: Song) {
        val currentPlaylist = state.value.playlist
        if (currentPlaylist != null) {
            val converters = Converters()
            val currentSongIds = converters.fromString(currentPlaylist.songIdsJson).toMutableList()
            currentSongIds.remove(songToRemove.id)
            val updatedSongIdsJson = converters.fromIntList(currentSongIds)

            val updatedPlaylist = currentPlaylist.copy(songIdsJson = updatedSongIdsJson)
            playlistRepository.updatePlaylist(updatedPlaylist)

            _state.update { it.copy(songWithMenu = null) }
        }
    }

    //  đổi từ Entity sang model
    @SuppressLint("DefaultLocale")
    private fun mapEntitiesToSongs(entities: List<SongEntity>): List<Song> {
        return entities.map { entity ->
            Song(
                id = entity.songId,
                title = entity.title,
                artist = entity.artist,
                duration = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(entity.durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(entity.durationMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(entity.durationMs))
                ),
                durationMs = entity.durationMs,
                filePath = entity.filePath,
                albumArtUri = entity.albumArtUri?.toUri()
            )
        }
    }
}