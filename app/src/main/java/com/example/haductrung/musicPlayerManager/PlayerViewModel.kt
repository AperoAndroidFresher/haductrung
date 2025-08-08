package com.example.haductrung.musicPlayerManager

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import com.example.haductrung.playback.MusicPlaybackService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
class PlayerViewModel(private val application: Application) : ViewModel() {

    private val _state = MutableStateFlow(PlayerUiState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<PlayerUiEvent>()
    val event = _event.asSharedFlow()

    // Biến để giữ "kênh giao tiếp" (Binder) tới Service
    private var musicBinder: MusicPlaybackService.MusicBinder? = null
    private var isServiceBound = false

    // Đối tượng quản lý kết nối tới Service
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as MusicPlaybackService.MusicBinder
            isServiceBound = true
            // Sau khi kết nối, có thể ra lệnh cho service chơi bài hát hiện tại trong state
            state.value.currentPlayingSong?.let {
                musicBinder?.playSong(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    fun processIntent(intent: PlayerUiIntent) {
        when (intent) {
            is PlayerUiIntent.PlaySong -> {
                _state.update {
                    it.copy(
                        currentPlayingSong = intent.song,
                        isPlaying = true
                    )
                }

                val serviceIntent = Intent(application, MusicPlaybackService::class.java).apply {
                    putExtra("SONG_TITLE", intent.song.title)
                    putExtra("SONG_FILE_PATH", intent.song.filePath)
                }

                // Bắt đầu và kết nối tới Service
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    application.startForegroundService(serviceIntent)
                } else {
                    application.startService(serviceIntent)
                }
                application.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
            }

            is PlayerUiIntent.TogglePlayPause -> {
                _state.update { it.copy(isPlaying = !it.isPlaying) }
                // Gửi lệnh qua Binder
                musicBinder?.togglePlayPause()
            }

            is PlayerUiIntent.DismissPlayer -> {
                _state.update { PlayerUiState() } // Reset state
                // Dừng và hủy kết nối Service
                if (isServiceBound) {
                    application.unbindService(connection)
                    isServiceBound = false
                }
                application.stopService(Intent(application, MusicPlaybackService::class.java))
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        if (isServiceBound) {
            application.unbindService(connection)
            isServiceBound = false
        }
    }
}