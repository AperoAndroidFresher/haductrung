package com.example.haductrung.playback


import android.app.Notification

import android.app.NotificationChannel

import android.app.NotificationManager
import android.app.PendingIntent

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.media.MediaPlayer
import android.net.Uri

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
import com.example.haductrung.MainActivity

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
    private val _currentSongFlow = MutableStateFlow<Song?>(null)
    val currentSongFlow: StateFlow<Song?> = _currentSongFlow
    private val _isPlayingState = MutableStateFlow(false)
    val isPlayingState: StateFlow<Boolean> = _isPlayingState
    private var isShuffleEnabled = false
    private val _isShuffleEnabledState = MutableStateFlow(false)
    val isShuffleEnabledState: StateFlow<Boolean> = _isShuffleEnabledState
    private var shuffledPlaylist: List<Song>? = null
    private var shuffledIndex: Int = -1

    private var currentSong: Song? = null
    private var currentPlaylist: List<Song>? = null
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
    fun toggleShuffle() {
        val newState = !isShuffleEnabled
        isShuffleEnabled = newState
        _isShuffleEnabledState.value = newState

        if (newState) {
            setLooping(false)
            shuffledPlaylist = currentPlaylist?.shuffled()
            shuffledIndex = 0
            currentSong = shuffledPlaylist?.get(shuffledIndex)
            currentSong?.let {
                startPlaying(it, currentPlaylist)
            }
        } else {
            shuffledPlaylist = null
            shuffledIndex = -1
        }
    }
    fun playNext() {
        val listToPlay = if (isShuffleEnabled) shuffledPlaylist else currentPlaylist
        if (listToPlay.isNullOrEmpty()) return

        val currentIndex = if (isShuffleEnabled) {
            shuffledIndex
        } else {
            listToPlay.indexOf(currentSong)
        }

        if (currentIndex != -1) {
            val nextIndex = if (isLoopingEnabled || isShuffleEnabled) {
                (currentIndex + 1) % listToPlay.size
            } else {
                if (currentIndex < listToPlay.size - 1) {
                    currentIndex + 1
                } else {
                    return
                }
            }
            val nextSong = listToPlay[nextIndex]
            if (isShuffleEnabled) {
                shuffledIndex = nextIndex
                startPlaying(nextSong, currentPlaylist)
            } else {
                startPlaying(nextSong, currentPlaylist)
            }
        }
    }

    fun playPrevious() {
        val listToPlay = if (isShuffleEnabled) shuffledPlaylist else currentPlaylist
        if (listToPlay.isNullOrEmpty()) return

        val currentIndex = if (isShuffleEnabled) {
            shuffledIndex
        } else {
            listToPlay.indexOf(currentSong)
        }

        if (currentIndex != -1) {
            val prevIndex = if (isLoopingEnabled || isShuffleEnabled) {
                (currentIndex - 1 + listToPlay.size) % listToPlay.size
            } else {
                if (currentIndex > 0) {
                    currentIndex - 1
                } else {
                    return
                }
            }
            val previousSong = listToPlay[prevIndex]
            if (isShuffleEnabled) {
                shuffledIndex = prevIndex
                startPlaying(previousSong, currentPlaylist)
            } else {
                startPlaying(previousSong, currentPlaylist)
            }
        }
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
        fun playNext() {
            this@MusicPlaybackService.playNext()
        }

        fun playPrevious() {
            this@MusicPlaybackService.playPrevious()
        }
        fun getCurrentPlayerState(): CurrentPlayerState {

            return CurrentPlayerState(

                song = this@MusicPlaybackService.currentSong,
                isLoopingEnabled = this@MusicPlaybackService.isLoopingEnabled,
                playlist = this@MusicPlaybackService.currentPlaylist,
                isShuffleEnabled = this@MusicPlaybackService.isShuffleEnabled
            )
        }
        private val _isLoopingEnabledState = MutableStateFlow(false)
        val isLoopingEnabledState: StateFlow<Boolean> = _isLoopingEnabledState
        fun setLooping(isLooping: Boolean) {
            this@MusicPlaybackService.setLooping(isLooping)
            _isLoopingEnabledState.value = isLooping
        }
        fun toggleShuffle() {
            this@MusicPlaybackService.toggleShuffle()
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
        _currentSongFlow.value = song
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

                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, createNotification())
                }
                setOnCompletionListener { mp ->
                    stopProgressUpdates()
                    _playbackState.value = PlaybackState(
                        currentPosition = mp.duration.toLong(),
                        totalDuration = mp.duration.toLong()
                    )

                    // SỬA: Sử dụng listToPlay để quyết định bài tiếp theo
                    val listToPlay = if (isShuffleEnabled) shuffledPlaylist else currentPlaylist

                    if (!listToPlay.isNullOrEmpty()) {
                        val currentIndex = listToPlay.indexOf(this@MusicPlaybackService.currentSong)
                        if (currentIndex != -1) {
                            val isLastSong = currentIndex == listToPlay.size - 1
                            val shouldPlayNext = isLoopingEnabled || isShuffleEnabled || !isLastSong

                            if (shouldPlayNext) {
                                val nextIndex = (currentIndex + 1) % listToPlay.size
                                val nextSong = listToPlay[nextIndex]
                                startPlaying(nextSong, currentPlaylist)
                            } else {
                                _isPlayingState.value = false
                                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.notify(NOTIFICATION_ID, createNotification())
                            }
                        }
                    } else {
                        _isPlayingState.value = false
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(NOTIFICATION_ID, createNotification())
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
    private fun getAlbumArtBitmap(albumArtUri: Uri?): Bitmap? {
        if (albumArtUri == null) {
            return null
        }
        return try {
            contentResolver.openFileDescriptor(albumArtUri, "r")?.use { pfd ->
                BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
        val albumArtUri = currentSong?.albumArtUri


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
        // --- Bổ sung code hiển thị ảnh bìa vào đây ---
        val albumArtBitmap = getAlbumArtBitmap(albumArtUri)
        if (albumArtBitmap != null) {
            // Gán ảnh cho ảnh bìa nhỏ
            notificationLayout.setImageViewBitmap(R.id.album_art_small, albumArtBitmap)
        } else {
            // Sử dụng ảnh mặc định nếu không lấy được ảnh bìa
            notificationLayout.setImageViewResource(R.id.album_art_small, R.drawable.grainydays)
        }
        // --- Kết thúc phần bổ sung ---

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
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            action = "ACTION_OPEN_PLAYER_DETAIL"
        }

        val prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)
        val playPausePendingIntent = PendingIntent.getBroadcast(this, 1, playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextPendingIntent = PendingIntent.getBroadcast(this, 2, nextIntent, PendingIntent.FLAG_IMMUTABLE)
        val closePendingIntent = PendingIntent.getBroadcast(this, 3, closeIntent, PendingIntent.FLAG_IMMUTABLE)
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        // Gán PendingIntent cho các nút trong RemoteViews
        notificationLayout.setOnClickPendingIntent(R.id.btn_prev, prevPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_play_pause, playPausePendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_close, closePendingIntent)

        // Xây dựng Notification với layout tùy chỉnh
        return NotificationCompat.Builder(this, channelId)
            .setContentIntent(openAppPendingIntent)
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