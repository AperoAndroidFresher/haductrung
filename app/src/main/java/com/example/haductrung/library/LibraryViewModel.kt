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
import com.example.haductrung.repository.SongRepository
import com.example.haductrung.database.entity.SongEntity
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri
import com.example.haductrung.library.remote.ApiClient
import com.example.haductrung.library.remote.RemoteSong
import com.example.haductrung.repository.LibraryRepository
import com.example.haductrung.repository.Song
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


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
        observeLocalSongsFromDatabase()
    }

    fun processIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.OnTabSelected -> handleTabSelection(intent.tab)
            is LibraryIntent.RetryFetchRemoteSongs -> fetchRemoteSongs()
            is LibraryIntent.OnToggleViewClick -> _state.update { it.copy(isGridView = !it.isGridView) }
            is LibraryIntent.OnToggleSortClick -> _state.update { it.copy(isSortMode = !it.isSortMode) }
            is LibraryIntent.OnMoreClick -> _state.update { it.copy(songWithMenu = intent.song.id) }
            is LibraryIntent.OnDismissMenu -> _state.update { it.copy(songWithMenu = null) }
            is LibraryIntent.CheckAndLoadSongs -> checkPermissionAndLoad()
            is LibraryIntent.OnRequestPermissionAgain -> viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
            is LibraryIntent.OnAddToPlaylistClick -> navigateToAddToPlaylist(intent.song)
        }
    }

    private fun observeLocalSongsFromDatabase() {
        songRepository.getAllSongs()
            .onEach { songsFromDb ->
                _state.update { it.copy(songList = mapEntitiesToUiSongs(songsFromDb)) }
            }
            .launchIn(viewModelScope)
    }

    private fun handleTabSelection(tab: LibraryTab) {
        _state.update { it.copy(selectedTab = tab) }
        if (tab == LibraryTab.REMOTE) {
            fetchRemoteSongs()
        }
    }

    private fun fetchRemoteSongs() {
        //  gọi lại API nếu chưa có dữ liệu or lỗi
        if (state.value.remoteState is RemoteState.Success) return

        _state.update { it.copy(remoteState = RemoteState.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val remoteSongs = ApiClient.build().getRemoteSongs()
                val uiSongs = mapRemoteToUiSongs(remoteSongs)
                _state.update { it.copy(remoteState = RemoteState.Success(uiSongs)) }
            } catch (e: Exception) {
                _state.update { it.copy(remoteState = RemoteState.Error("Failed to fetch data")) }
            }
        }
    }

    private fun navigateToAddToPlaylist(song: Song) {
        viewModelScope.launch {
            _state.update { it.copy(songWithMenu = null) }
            _event.emit(LibraryEvent.NavigateToAddToPlaylistScreen(song))
        }
    }

    private fun checkPermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val isGranted = applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        _state.update { it.copy(hasPermission = isGranted) }
        if (isGranted) {
            syncSongsFromDeviceToDb()
        } else {
            viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
        }
    }

    private fun syncSongsFromDeviceToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceSongs = mediaStoreScanner.getAudioData()
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
    private fun mapEntitiesToUiSongs(entities: List<SongEntity>): List<Song> {
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

    @SuppressLint("DefaultLocale")
    private fun mapRemoteToUiSongs(remoteSongs: List<RemoteSong>): List<Song> {
        return remoteSongs.map { remote ->
            val durationMs = remote.duration.toLongOrNull() ?: 0L
            val durationFormatted = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(durationMs),
                TimeUnit.MILLISECONDS.toSeconds(durationMs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMs))
            )
            Song(
                id = remote.path.hashCode(),
                title = remote.title,
                artist = remote.artist,

                duration = durationFormatted,
                durationMs = durationMs,
                filePath = remote.path,
                albumArtUri = null
            )
        }
    }
}