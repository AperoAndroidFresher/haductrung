package com.example.haductrung.musicPlayerManager

import com.example.haductrung.repository.Song

data class PlayerUiState(
    val currentPlayingSong: Song? = null,
    val isPlaying: Boolean = false,
)
sealed interface PlayerUiIntent {
    data class PlaySong(val song: Song) : PlayerUiIntent
    data object TogglePlayPause : PlayerUiIntent
    data object DismissPlayer : PlayerUiIntent
}
sealed interface PlayerUiEvent {
}