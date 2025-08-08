package com.example.haductrung.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.nfc.Tag
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.haductrung.R
import com.example.haductrung.repository.Song
import androidx.core.net.toUri

class MusicPlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()

    /**
     * Lớp Binder hoạt động như "kênh giao tiếp" cho ViewModel.
     */
    inner class MusicBinder : Binder() {
        // ViewModel sẽ gọi các hàm này
        fun playSong(song: Song) {
            this@MusicPlaybackService.startPlaying(song)
        }

        fun togglePlayPause() {
            this@MusicPlaybackService.togglePlayPause()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * Được gọi khi service bắt đầu.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val songTitle = intent?.getStringExtra("SONG_TITLE") ?: "Now Playing"
        val songFilePath = intent?.getStringExtra("SONG_FILE_PATH")

        // Hiển thị notification để đưa service lên chế độ foreground
        startForeground(NOTIFICATION_ID, createNotification(songTitle))

        // Bắt đầu phát nhạc từ intent
        if (songFilePath != null) {
            startPlayingWithFilePath(songFilePath)
        }

        return START_STICKY // Tự động khởi động lại service nếu bị hệ thống kill
    }

    /**
     * Dọn dẹp tài nguyên khi service bị hủy.
     */
    override fun onDestroy() {
        super.onDestroy()
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
            }
        } catch (e: Exception) {
            e.printStackTrace() // In lỗi ra nếu có vấn đề
        }
    }

    private fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
            }
        }
    }

    /**
     * Hàm phụ để tạo và cấu hình Notification.
     */
    private fun createNotification(songTitle: String): Notification {
        val channelId = "music_playback_channel"
        Log.d("tag", "createNotification: Tạo notification cho bài hát: $songTitle")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Music Playback"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("tag", "createNotification: Kênh thông báo đã được tạo (API >= 26). Importance: $importance")
        } else {
            Log.d("tag", "createNotification: Tạo notification (API < 26).")
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
        private const val CHANNEL_ID = "music_playback_channel"
    }
}