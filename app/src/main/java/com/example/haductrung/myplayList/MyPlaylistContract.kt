package com.example.haductrung.myplayList

import com.example.haductrung.library.minicomposable.Song


data class MyPlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val playlistIdWithMenu: Int? = null
)


sealed interface MyPlaylistIntent {
    data class AddSongToPlaylist(val song: Song, val playlistId: Int) : MyPlaylistIntent
    data class CreatePlaylistClicked(val playlistName: String) : MyPlaylistIntent
    data class PlaylistClicked(val playlistId: Int) : MyPlaylistIntent
    data class MoreOptionsClicked(val playlistId: Int) : MyPlaylistIntent
    data object DismissMenu : MyPlaylistIntent
    data class DeletePlaylistClicked(val playlistId: Int) : MyPlaylistIntent
}

sealed interface MyPlaylistEvent {
    data class NavigateToPlaylistDetail(val playlistId: Int) : MyPlaylistEvent
}