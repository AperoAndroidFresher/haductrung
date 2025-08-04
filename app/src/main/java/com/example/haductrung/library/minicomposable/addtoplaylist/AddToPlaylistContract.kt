package com.example.haductrung.library.minicomposable.addtoplaylist

import com.example.haductrung.myplaylist.Playlist


data class AddToPlaylistState(
    val isLoading: Boolean = true,
    val playlists: List<Playlist> = emptyList(),
    // luwu id bai hat can them
    val songIdToAdd: Int? = null
)


sealed interface AddToPlaylistIntent {
    // tải ds các playlist hiện có
    data class LoadPlaylists(val songId: Int) : AddToPlaylistIntent
    data class OnPlaylistSelected(val playlist: Playlist) : AddToPlaylistIntent
    data object NavigateToMyPlaylistTab : AddToPlaylistIntent
}


sealed interface AddToPlaylistEvent {
    data object GoBack : AddToPlaylistEvent
    data object NavigateToMyPlaylist : AddToPlaylistEvent
    data class ShowSuccessMessage(val playlistName: String) : AddToPlaylistEvent
}