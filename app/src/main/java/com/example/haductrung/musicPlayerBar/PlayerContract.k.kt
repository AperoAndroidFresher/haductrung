package com.example.haductrung.musicPlayerBar

import com.example.haductrung.repository.Song

data class PlaybackState(
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L
)
data class PlayerUiState(
    val currentPlayingSong: Song? = null,
    val currentPlaylist: List<Song>? = null,
    val currentSongIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val progress: Float = 0f,
    val isDetailScreenVisible: Boolean = false
)
sealed interface PlayerUiIntent {
    data class PlaySong(val song: Song, val playlist: List<Song>? = null) : PlayerUiIntent
    data object TogglePlayPause : PlayerUiIntent
    data object DismissPlayer : PlayerUiIntent
//    data class Seek(val positionMs: Long) : PlayerUiIntent // kéo slider
    data object SkipToNext : PlayerUiIntent // next
    data object SkipToPrevious : PlayerUiIntent // previous
    data object OpenPlayerDetail : PlayerUiIntent
    data object BackFromPlayerDetail : PlayerUiIntent
}
sealed interface PlayerUiEvent {
}