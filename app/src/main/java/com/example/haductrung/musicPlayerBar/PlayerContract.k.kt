package com.example.haductrung.musicPlayerBar

import com.example.haductrung.repository.Song

data class PlaybackState(
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L
)
data class CurrentPlayerState(
    val song: Song?,
    val playlist: List<Song>?,
    val isLoopingEnabled: Boolean,
    val isShuffleEnabled: Boolean
)
data class PlayerUiState(
    val currentPlayingSong: Song? = null,
    val currentPlaylist: List<Song>? = null,
    val currentSongIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val progress: Float = 0f,
    val isDetailScreenVisible: Boolean = false,
    val isShuffleEnabled: Boolean = false,
    val isLoopingEnabled: Boolean = false,
    val playHistory: List<Song> = emptyList()
)
sealed interface PlayerUiIntent {
    data class PlaySong(val song: Song, val playlist: List<Song>? = null) : PlayerUiIntent
    data object TogglePlayPause : PlayerUiIntent
    data object DismissPlayer : PlayerUiIntent
    data class Seek(val position: Float) : PlayerUiIntent
    data object SkipToNext : PlayerUiIntent
    data object SkipToPrevious : PlayerUiIntent
    data object OpenPlayerDetail : PlayerUiIntent
    data object BackFromPlayerDetail : PlayerUiIntent
    data class ScreenChanged(val route: String?) : PlayerUiIntent
    data object AppEnteredBackground : PlayerUiIntent
    data object ToggleShuffle : PlayerUiIntent
    data object ToggleLoopMode : PlayerUiIntent
}
sealed interface PlayerUiEvent {
}