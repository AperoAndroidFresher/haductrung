package com.example.haductrung.myplaylist

import java.util.UUID


data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val songIds: List<Int> = emptyList()
)

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val isGridView: Boolean = false,
    val isSortMode: Boolean = false,
    val playlistWithMenu: String? = null,
    val showCreatePlaylistDialog: Boolean = false,
    val playlistToRename: Playlist? = null
)
sealed interface PlaylistIntent {
    data object OnToggleViewClick : PlaylistIntent
    data object OnToggleSortClick : PlaylistIntent
    data class OnMoreClick(val playlist: Playlist) : PlaylistIntent
    data object OnDismissMenu : PlaylistIntent
    data object OnCreatePlaylistClick : PlaylistIntent
    data object OnDismissCreatePlaylistDialog : PlaylistIntent
    data class OnConfirmCreatePlaylist(val name: String) : PlaylistIntent
    data class OnRemovePlaylist(val playlist: Playlist) : PlaylistIntent
    data class OnRenamePlaylistClick(val playlist: Playlist) : PlaylistIntent
    data object OnDismissRenamePlaylistDialog : PlaylistIntent
    data class OnConfirmRenamePlaylist(val playlist: Playlist, val newName: String) : PlaylistIntent
    data class OnPlaylistClick(val playlist: Playlist) : PlaylistIntent
}

sealed interface PlaylistEvent {
    data class NavigateToPlaylistDetail(val playlistId: String) : PlaylistEvent
}