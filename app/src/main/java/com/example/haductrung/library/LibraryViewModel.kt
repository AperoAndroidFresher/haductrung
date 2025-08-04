package com.example.haductrung.library

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.Manifest
import android.annotation.SuppressLint
import com.example.haductrung.SongRepository
import com.example.haductrung.database.entity.SongEntity
import com.example.haductrung.library.Song
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri


class LibraryViewModel(
    private val songRepository: SongRepository,
    private val mediaStoreScanner: LibraryRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<LibraryEvent>()
    val event = _event.asSharedFlow()

    init {

        observeSongsFromDatabase()
    }

    private fun observeSongsFromDatabase() {
        viewModelScope.launch {
            songRepository.getAllSongs().collect { songsFromDb ->
                _state.update { currentState ->
                    val mappedSongs = mapEntitiesToSongs(songsFromDb)
                    val finalList = if (currentState.isSortMode) {
                        mappedSongs.sortedBy { it.title }
                    } else {
                        mappedSongs
                    }
                    currentState.copy(songList = finalList)
                }
            }
        }
    }

    fun processIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.onToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }
            is LibraryIntent.onToggleSortClick -> {
                val currentState = _state.value
                val sortedList = if (!currentState.isSortMode) {
                    currentState.songList.sortedBy { it.title }
                } else {
                    currentState.songList
                }
                _state.update { it.copy(isSortMode = !it.isSortMode, songList = sortedList) }
            }
            is LibraryIntent.onMoreClick -> {
                _state.update { it.copy(songWithMenu = intent.song.id) }
            }
            is LibraryIntent.onDismissMenu -> {
                _state.update { it.copy(songWithMenu = null) }
            }
            is LibraryIntent.CheckAndLoadSongs -> {
                checkPermissionAndLoad()
            }
            is LibraryIntent.onRequestPermissionAgain -> {
                viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
            }
            is LibraryIntent.OnAddToPlaylistClick -> {
                viewModelScope.launch {
                    _state.update { it.copy(songWithMenu = null) }
                    _event.emit(LibraryEvent.NavigateToAddToPlaylistScreen(intent.song))
                }
            }
            else -> {}
        }
    }

    private fun checkPermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isGranted =
            applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

        _state.update { it.copy(hasPermission = isGranted) }

        if (isGranted) {
            syncSongsFromDeviceToDb()
        } else {
            viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
        }
    }
    private fun syncSongsFromDeviceToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            // Lấy danh sách bài hát từ bộ nhớ máy
            val deviceSongs = mediaStoreScanner.getAudioData()

            // Chuyển đổi sang dạng Entity của database
            val songEntities = deviceSongs.map { song ->
                SongEntity(
                    songId = song.id,
                    title = song.title,
                    artist = song.artist,
                    durationMs = song.durationMs,
                    filePath = song.filePath,
                    albumArtUri = song.albumArtUri?.toString()
                )
            }

            songRepository.insertAll(songEntities)

        }
    }

    @SuppressLint("DefaultLocale")
    private fun mapEntitiesToSongs(entities: List<SongEntity>): List<Song> {
        return entities.map { entity ->
            Song(
                id = entity.songId,
                title = entity.title,
                artist = entity.artist,
                duration = String.format("%02d:%02d",
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