package com.example.haductrung.playback


import android.app.Notification

import android.app.NotificationChannel

import android.app.NotificationManager
import android.app.PendingIntent

import android.content.Intent

import android.media.MediaPlayer

import android.os.Binder

import android.os.Build

import android.os.IBinder
import android.widget.RemoteViews

import androidx.core.app.NotificationCompat

import com.example.haductrung.R

import com.example.haductrung.repository.Song

import androidx.core.net.toUri

import androidx.lifecycle.LifecycleService

import androidx.lifecycle.lifecycleScope

import com.example.haductrung.musicPlayerBar.CurrentPlayerState

import com.example.haductrung.musicPlayerBar.PlaybackState

import com.example.haductrung.musicPlayerBar.PlayerUiIntent

import com.example.haductrung.playback.PlayerManager.viewModel

import kotlinx.coroutines.Job

import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableSharedFlow

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asSharedFlow

import kotlinx.coroutines.launch


class MusicPlaybackService : LifecycleService() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState
    private var currentSong: Song? = null

    private var currentPlaylist: List<Song>? = null

    private val _isPlayingState = MutableStateFlow(false)

   // val isPlayingState: StateFlow<Boolean> = _isPlayingState

    //private val _songCompletion = MutableSharedFlow<Unit>()

   // val songCompletion = _songCompletion.asSharedFlow()

    private var isPersistent = false

    private var isLoopingEnabled = false

    private var progressJob: Job? = null

    fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        mediaPlayer?.let {
            _playbackState.value = _playbackState.value.copy(
                currentPosition = it.currentPosition.toLong()
            )
        }
    }

    fun updateNowPlayingInfo(song: Song?, playlist: List<Song>?) {
        this.currentSong = song
        this.currentPlaylist = playlist
    }

    fun setLooping(isLooping: Boolean) {
        this.isLoopingEnabled = isLooping
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlaybackService = this@MusicPlaybackService
        fun togglePlayPause() {
            this@MusicPlaybackService.togglePlayPause()
        }

        fun seekTo(positionMs: Long) {
            this@MusicPlaybackService.seekTo(positionMs)
        }

        fun updateNowPlayingInfo(song: Song?, playlist: List<Song>?) {
            this@MusicPlaybackService.updateNowPlayingInfo(song, playlist)
        }

        fun getCurrentPlayerState(): CurrentPlayerState {

            return CurrentPlayerState(

                song = this@MusicPlaybackService.currentSong,

                playlist = this@MusicPlaybackService.currentPlaylist
            )
        }

        fun setLooping(isLooping: Boolean) {
            this@MusicPlaybackService.setLooping(isLooping)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isPersistent = intent?.getBooleanExtra("IS_PERSISTENT", false) ?: false
        val songFilePath = intent?.getStringExtra("SONG_FILE_PATH")
        startForeground(NOTIFICATION_ID, createNotification())
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

    private fun startPlaying(song: Song, playlist: List<Song>?) {
        this.currentSong = song
        this.currentPlaylist = playlist
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
        startPlayingWithFilePath(song.filePath)
    }

    private fun startPlayingWithFilePath(filePath: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, filePath.toUri())
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlayingState.value = true
                    startProgressUpdates()
                }
                setOnCompletionListener { mp ->
                    stopProgressUpdates()
                    _playbackState.value = PlaybackState(
                        currentPosition = mp.duration.toLong(),
                        totalDuration = mp.duration.toLong()
                    )
                    val currentPlaylist = this@MusicPlaybackService.currentPlaylist
                    if (!currentPlaylist.isNullOrEmpty()) {
                        val currentIndex =
                            currentPlaylist.indexOf(this@MusicPlaybackService.currentSong)
                        if (currentIndex != -1) {
                            val isLastSongAndNotLooping =
                                (currentIndex == currentPlaylist.size - 1) && !isLoopingEnabled
                            if (isLastSongAndNotLooping) {
                                _isPlayingState.value = false
                                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.notify(NOTIFICATION_ID, createNotification()) // Cập nhật notification
                            } else {
                                val nextIndex = (currentIndex + 1) % currentPlaylist.size
                                val nextSong = currentPlaylist[nextIndex]
                                startPlaying(nextSong, currentPlaylist)
                            }
                        }
                    } else {
                        _isPlayingState.value = false
                    }
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
                stopProgressUpdates()
            } else {
                it.start()
                startProgressUpdates()
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        if (!isPersistent) {
            stopSelf()
        }
    }

    private fun createNotification(): Notification {
        val channelId = "music_playback_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Music Playback"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Lấy thông tin bài hát và playlist từ Service
        val songTitle = currentSong?.title ?: "Unknown Title"
        val songArtist = currentSong?.artist ?: "Unknown Artist"
        val isPlaying = mediaPlayer?.isPlaying ?: false

        // Tạo RemoteViews từ layout tùy chỉnh
        val notificationLayout = RemoteViews(packageName, R.layout.custom_notification)

        // Cập nhật TextView cho tiêu đề bài hát và nghệ sĩ
        notificationLayout.setTextViewText(R.id.song_title, songTitle)
        notificationLayout.setTextViewText(R.id.song_artist, songArtist)

        // Cập nhật TextView cho vị trí bài hát trong playlist (ví dụ: 5/10)
        currentPlaylist?.let { playlist ->
            val currentIndex = playlist.indexOf(currentSong)
            if (currentIndex != -1) {
                val extraText = "${currentIndex + 1}/${playlist.size}"
                notificationLayout.setTextViewText(R.id.extra_text, extraText)
            } else {
                notificationLayout.setTextViewText(R.id.extra_text, "")
            }
        } ?: run {
            notificationLayout.setTextViewText(R.id.extra_text, "")
        }

        // Hiển thị icon Play/Pause đúng với trạng thái hiện tại
        notificationLayout.setImageViewResource(
            R.id.btn_play_pause,
            if (isPlaying) R.drawable.pause else R.drawable.play
        )

        // Tạo các Intent và PendingIntent cho các nút điều khiển
        val prevIntent = Intent("ACTION_PREVIOUS").setPackage(packageName)
        val playPauseIntent = Intent("ACTION_PLAY_PAUSE").setPackage(packageName)
        val nextIntent = Intent("ACTION_NEXT").setPackage(packageName)
        val closeIntent = Intent("ACTION_CLOSE").setPackage(packageName)

        val prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)
        val playPausePendingIntent = PendingIntent.getBroadcast(this, 1, playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextPendingIntent = PendingIntent.getBroadcast(this, 2, nextIntent, PendingIntent.FLAG_IMMUTABLE)
        val closePendingIntent = PendingIntent.getBroadcast(this, 3, closeIntent, PendingIntent.FLAG_IMMUTABLE)

        // Gán PendingIntent cho các nút trong RemoteViews
        notificationLayout.setOnClickPendingIntent(R.id.btn_prev, prevPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_play_pause, playPausePendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_close, closePendingIntent)

        // Xây dựng Notification với layout tùy chỉnh
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.play)
            .setCustomContentView(notificationLayout)
            .setOngoing(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }
/// dang oke day
    companion object {
        private const val NOTIFICATION_ID = 1
    }
}