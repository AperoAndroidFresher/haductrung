package com.example.haductrung.library.addtoplaylist

import com.example.haductrung.database.entity.PlaylistEntity


data class AddToPlaylistState(
    val isLoading: Boolean = true,
    val playlists: List<PlaylistEntity> = emptyList(),
    // luwu id bai hat can them
    val songIdToAdd: Int? = null
)


sealed interface AddToPlaylistIntent {
    // tải ds các playlist hiện có
    data class LoadPlaylists(val songId: Int) : AddToPlaylistIntent
    data class OnPlaylistSelected(val playlist: PlaylistEntity) : AddToPlaylistIntent
    data object NavigateToMyPlaylistTab : AddToPlaylistIntent
}


sealed interface AddToPlaylistEvent {
    data object GoBack : AddToPlaylistEvent
    data object NavigateToMyPlaylist : AddToPlaylistEvent
    data class ShowSuccessMessage(val playlistName: String) : AddToPlaylistEvent
}