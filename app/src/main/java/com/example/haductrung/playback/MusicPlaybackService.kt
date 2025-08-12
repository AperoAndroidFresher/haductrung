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

    private fun startPlaying(song: Song, playlist: List<Song>?) {
        this.currentSong = song
        this.currentPlaylist = playlist
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(song.title))
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
/// dang oke day
    companion object {
        private const val NOTIFICATION_ID = 1
    }
}