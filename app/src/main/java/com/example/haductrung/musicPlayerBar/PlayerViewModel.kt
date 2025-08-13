package com.example.haductrung.musicPlayerBar

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.Home
import com.example.haductrung.home.Home
import com.example.haductrung.playback.MusicPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(private val application: Application) : ViewModel() {

    private val _state = MutableStateFlow(PlayerUiState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<PlayerUiEvent>()
    val event = _event.asSharedFlow()

    private var musicBinder: MusicPlaybackService.MusicBinder? = null
    private var isServiceBound = false
    private var serviceJob: Job? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as MusicPlaybackService.MusicBinder
            isServiceBound = true
            val musicService = musicBinder?.getService()

            val serviceState = musicBinder?.getCurrentPlayerState()
            val currentVmState = state.value

            if (currentVmState.currentPlayingSong == null && serviceState?.song != null) {
                val restoredSong = serviceState.song
                val restoredPlaylist = serviceState.playlist
                val restoredIndex = restoredPlaylist?.indexOf(restoredSong) ?: -1

                _state.update {
                    it.copy(
                        currentPlayingSong = restoredSong,
                        currentPlaylist = restoredPlaylist,
                        currentSongIndex = restoredIndex,
                        isPlaying = true,
                        isLoopingEnabled = serviceState.isLoopingEnabled
                    )
                }
            }
            else {
                musicBinder?.updateNowPlayingInfo(currentVmState.currentPlayingSong, currentVmState.currentPlaylist)
            }

            serviceJob = viewModelScope.launch {
                // 1. Theo dõi bài hát hiện tại từ Service (đây là phần bạn cần thêm)
                launch {
                    musicService?.currentSongFlow
                        ?.filterNotNull()
                        ?.collect { song ->
                            _state.update { currentState ->
                                val newIndex = currentState.currentPlaylist?.indexOf(song) ?: -1
                                currentState.copy(
                                    currentPlayingSong = song,
                                    currentSongIndex = newIndex,
                                    isPlaying = true
                                )
                            }
                        }
                }
                launch {
                    musicService?.playbackState?.collect { playbackState ->
                        _state.update { currentState ->
                            currentState.copy(
                                currentPosition = playbackState.currentPosition,
                                totalDuration = playbackState.totalDuration,
                                progress = if (playbackState.totalDuration > 0) {
                                    playbackState.currentPosition.toFloat() / playbackState.totalDuration
                                } else 0f
                            )
                        }
                    }
                }
                launch {
                    musicService?.isPlayingState?.collect { isPlaying ->
                        _state.update { currentState ->
                            currentState.copy(
                                isPlaying = isPlaying
                            )
                        }
                    }
                }
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            musicBinder = null
            serviceJob?.cancel()
        }
    }
    fun processIntent(intent: PlayerUiIntent) {
        when (intent) {
            is PlayerUiIntent.PlaySong -> {
                val isSameSong = state.value.currentPlayingSong?.id == intent.song.id
                if (isSameSong) {
                    processIntent(PlayerUiIntent.TogglePlayPause)
                    return
                }

                if (isServiceBound) {
                    try {
                        application.unbindService(connection)
                    } catch (e: IllegalArgumentException) {
                    }
                }
                application.stopService(Intent(application, MusicPlaybackService::class.java))

                _state.update {
                    val newPlaylist = intent.playlist
                    val newIndex = newPlaylist?.indexOf(intent.song) ?: -1
                    it.copy(
                        currentPlayingSong = intent.song,
                        isPlaying = true,
                        currentPlaylist = newPlaylist,
                        currentSongIndex = newIndex
                    )
                }
                val isPersistent = intent.playlist != null
                val serviceIntent = Intent(application, MusicPlaybackService::class.java).apply {
                    putExtra("SONG_TITLE", intent.song.title)
                    putExtra("SONG_FILE_PATH", intent.song.filePath)
                    putExtra("IS_PERSISTENT", isPersistent)
                    putExtra("IS_LOOPING_ENABLED", state.value.isLoopingEnabled)
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    application.startForegroundService(serviceIntent)
                } else {
                    application.startService(serviceIntent)
                }
                application.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
            }

            is PlayerUiIntent.TogglePlayPause -> {
                _state.update { it.copy(isPlaying = !it.isPlaying) }
                musicBinder?.togglePlayPause()
            }

            is PlayerUiIntent.DismissPlayer -> {
                if (isServiceBound) {
                    try {
                        application.unbindService(connection)
                    } catch (e: IllegalArgumentException) {
                    }
                }
                application.stopService(Intent(application, MusicPlaybackService::class.java))
                _state.update { PlayerUiState() }
            }

            is PlayerUiIntent.OpenPlayerDetail -> {
                _state.update { it.copy(isDetailScreenVisible = true) }
            }

            is PlayerUiIntent.BackFromPlayerDetail -> {
                _state.update { it.copy(isDetailScreenVisible = false) }
            }

            is PlayerUiIntent.SkipToNext -> {
                val currentState = state.value
                val currentPlaylist = currentState.currentPlaylist
                if (!currentPlaylist.isNullOrEmpty()) {
                    val nextIndex = if (currentState.isLoopingEnabled) {
                        (currentState.currentSongIndex + 1) % currentPlaylist.size
                    } else {

                        if (currentState.currentSongIndex < currentPlaylist.size - 1) {
                            currentState.currentSongIndex + 1
                        } else {
                            return
                        }
                    }
                    val nextSong = currentPlaylist[nextIndex]
                    processIntent(PlayerUiIntent.PlaySong(nextSong, currentPlaylist))
                }
            }
            is PlayerUiIntent.SkipToPrevious -> {
                val currentState = state.value
                val currentPlaylist = currentState.currentPlaylist
                if (!currentPlaylist.isNullOrEmpty()) {
                    val previousIndex = if (currentState.isLoopingEnabled) {
                        (currentState.currentSongIndex - 1 + currentPlaylist.size) % currentPlaylist.size
                    } else {
                        if (currentState.currentSongIndex > 0) {
                            currentState.currentSongIndex - 1
                        } else {
                            return
                        }
                    }
                    val previousSong = currentPlaylist[previousIndex]
                    processIntent(PlayerUiIntent.PlaySong(previousSong, currentPlaylist))
                }
            }
            is PlayerUiIntent.Seek -> {
                val totalDuration = state.value.totalDuration
                if (totalDuration > 0) {
                    val seekToMs = (totalDuration * intent.position).toLong()
                    musicBinder?.seekTo(seekToMs)
                }
            }
            is PlayerUiIntent.ScreenChanged -> {
                val currentState = state.value
                val isPlayingFromLibrary = currentState.currentPlayingSong != null && currentState.currentPlaylist == null
                if (intent.route == Home::class.qualifiedName && isPlayingFromLibrary) {
                    processIntent(PlayerUiIntent.DismissPlayer)
                }
            }
            is PlayerUiIntent.AppEnteredBackground -> {
                val currentState = state.value
                val isPlayingFromLibrary = currentState.currentPlayingSong != null && currentState.currentPlaylist == null

                if (isPlayingFromLibrary) {
                    processIntent(PlayerUiIntent.DismissPlayer)
                }
            }
            is PlayerUiIntent.ToggleShuffle -> {
                _state.update { it.copy(isShuffleEnabled = !it.isShuffleEnabled) }
            }
            is PlayerUiIntent.ToggleLoopMode -> {
                val newLoopingState = !state.value.isLoopingEnabled
                _state.update { it.copy(isLoopingEnabled = newLoopingState) }
                musicBinder?.setLooping(newLoopingState)
            }
        }
    }
    fun checkAndRestoreState() {
        if (!isServiceBound) {
            val serviceIntent = Intent(application, MusicPlaybackService::class.java)
            application.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }
    }
    override fun onCleared() {
        super.onCleared()
        serviceJob?.cancel()
        if (isServiceBound) {
            application.unbindService(connection)
            isServiceBound = false
        }
    }
}