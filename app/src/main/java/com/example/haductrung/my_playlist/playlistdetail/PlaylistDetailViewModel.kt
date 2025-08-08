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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlaylistDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistDetailState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        val playlistId: String? = savedStateHandle["playlistId"]
        playlistId?.toIntOrNull()?.let { id ->
            observePlaylistDetails(id)
        }
    }

    fun processIntent(intent: PlaylistDetailIntent) {
        when (intent) {
            is PlaylistDetailIntent.OnToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }
            is PlaylistDetailIntent.OnMoreClick -> {
                _state.update { it.copy(
                    songWithMenu = intent.song.id,
                    selectedSongId = intent.song.id
                ) }
            }
            is PlaylistDetailIntent.OnDismissMenu -> {
                _state.update { it.copy(songWithMenu = null) }
            }
            is PlaylistDetailIntent.OnDeleteSongFromPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    removeSongFromPlaylist(intent.song)
                }
            }
            is PlaylistDetailIntent.OnSongSelected -> {
                _state.update { it.copy(selectedSongId = intent.songId) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePlaylistDetails(playlistId: Int) {
        viewModelScope.launch {
            playlistRepository.findPlaylistById(playlistId)
                .filterNotNull()
                .flatMapLatest { playlistEntity ->
                    _state.update { it.copy(playlist = playlistEntity) }
                    val songIds = Converters().fromString(playlistEntity.songIdsJson)
                    songRepository.getSongsByIds(songIds)
                }
                .map { songEntities ->
                    mapEntitiesToSongs(songEntities)
                }
                .collect { songs ->
                    _state.update { it.copy(isLoading = false, songs = songs) }
                }
        }
    }

    private suspend fun removeSongFromPlaylist(songToRemove: Song) {
        state.value.playlist?.let { currentPlaylist ->
            val converters = Converters()
            val updatedSongIds = converters.fromString(currentPlaylist.songIdsJson)
                .filter { it != songToRemove.id }

            val updatedPlaylist = currentPlaylist.copy(
                songIdsJson = converters.fromIntList(updatedSongIds)
            )

            playlistRepository.updatePlaylist(updatedPlaylist)
            _state.update { it.copy(songWithMenu = null) }
        }
    }

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