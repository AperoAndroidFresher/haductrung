package com.example.haductrung.musicPlayerBar

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.playback.MusicPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

            // Lấy service instance và bắt đầu lắng nghe tiến trình
            val musicService = musicBinder?.getService()

            serviceJob = viewModelScope.launch {
                musicService?.playbackState?.collect { playbackState ->
                    // Cập nhật state của ViewModel với dữ liệu từ service
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

                val serviceIntent = Intent(application, MusicPlaybackService::class.java).apply {
                    putExtra("SONG_TITLE", intent.song.title)
                    putExtra("SONG_FILE_PATH", intent.song.filePath)
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
                    val nextIndex = (currentState.currentSongIndex + 1) % currentPlaylist.size
                    val nextSong = currentPlaylist[nextIndex]
                    processIntent(PlayerUiIntent.PlaySong(nextSong, currentPlaylist))
                }
            }

            is PlayerUiIntent.SkipToPrevious -> {
                val currentState = state.value
                val currentPlaylist = currentState.currentPlaylist
                if (!currentPlaylist.isNullOrEmpty()) {
                    val previousIndex = (currentState.currentSongIndex - 1 + currentPlaylist.size) % currentPlaylist.size
                    val previousSong = currentPlaylist[previousIndex]
                    processIntent(PlayerUiIntent.PlaySong(previousSong, currentPlaylist))
                }
            }
//            is PlayerUiIntent.Seek -> {
//                musicBinder?.seekTo(intent.positionMs)
//            }
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