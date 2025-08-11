package com.example.haductrung.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.haductrung.R
import com.example.haductrung.repository.Song
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.haductrung.musicPlayerBar.PlaybackState
import com.example.haductrung.musicPlayerBar.PlayerUiIntent
import com.example.haductrung.playback.PlayerManager.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicPlaybackService : LifecycleService() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()

    // THÊM MỚI: Các biến để quản lý và phát sóng tiến trình
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState
    private var progressJob: Job? = null
//    fun seekTo(positionMs: Long) {
//        mediaPlayer?.seekTo(positionMs.toInt())
//    }

    inner class MusicBinder : Binder() {
        // THÊM MỚI: Trả về instance của Service
        fun getService(): MusicPlaybackService = this@MusicPlaybackService

        // Giữ lại các hàm cũ để ViewModel không bị lỗi
        fun togglePlayPause() {
            this@MusicPlaybackService.togglePlayPause()
        }
//        fun seekTo(positionMs: Long) {
//            this@MusicPlaybackService.seekTo(positionMs)
//        }
//        fun playNewSong(song: Song) {
//            this@MusicPlaybackService.startPlaying(song)
//        }
    }

    override fun onBind(intent: Intent): IBinder {
        // THÊM MỚI: Dòng này quan trọng với LifecycleService
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val songTitle = intent?.getStringExtra("SONG_TITLE") ?: "Now Playing"
        val songFilePath = intent?.getStringExtra("SONG_FILE_PATH")

        startForeground(NOTIFICATION_ID, createNotification(songTitle))

        if (songFilePath != null) {
            startPlayingWithFilePath(songFilePath)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdates()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startPlaying(song: Song) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(song.title))
        startPlayingWithFilePath(song.filePath)
    }

    private fun startPlayingWithFilePath(filePath: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, filePath.toUri())
                prepare()
                start()
                startProgressUpdates()
                setOnCompletionListener {mp ->
                    stopProgressUpdates()
                    _playbackState.value = PlaybackState(
                        currentPosition = mp.duration.toLong(),
                        totalDuration = mp.duration.toLong()
                    )
                    viewModel.processIntent(PlayerUiIntent.SkipToNext)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                // THÊM MỚI: Dừng cập nhật khi tạm dừng
                stopProgressUpdates()
            } else {
                it.start()
                // THÊM MỚI: Tiếp tục cập nhật khi phát lại
                startProgressUpdates()
            }
        }
    }

    // THÊM MỚI: Hàm bắt đầu cập nhật tiến trình
    private fun startProgressUpdates() {
        progressJob?.cancel() // Hủy job cũ nếu có
        progressJob = lifecycleScope.launch {
            while (true) {
                mediaPlayer?.let { mp ->

                    if (mp.isPlaying) {
                        _playbackState.value = PlaybackState(
                            currentPosition = mp.currentPosition.toLong(),
                            totalDuration = mp.duration.toLong()
                        )
                    }
                }
                delay(200)
            }
        }
    }
    private fun stopProgressUpdates() {
        progressJob?.cancel()
    }

    private fun createNotification(songTitle: String): Notification {

        val channelId = "music_playback_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Music Playback"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Now Playing")
            .setContentText(songTitle)
            .setSmallIcon(R.drawable.play)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}