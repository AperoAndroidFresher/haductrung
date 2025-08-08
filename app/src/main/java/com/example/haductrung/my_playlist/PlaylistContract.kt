package com.example.haductrung.my_playlist

import com.example.haductrung.database.entity.PlaylistEntity

data class PlaylistState(
    val playlists: List<PlaylistEntity> = emptyList(),
    val isGridView: Boolean = false,
    val isSortMode: Boolean = false,
    val playlistWithMenu: String? = null,
    val showCreatePlaylistDialog: Boolean = false,
    val playlistToRename: PlaylistEntity? = null
)
sealed interface PlaylistIntent {
    data object OnToggleViewClick : PlaylistIntent
    data class OnMoreClick(val playlist: PlaylistEntity) : PlaylistIntent
    data object OnDismissMenu : PlaylistIntent
    data object OnCreatePlaylistClick : PlaylistIntent
    data object OnDismissCreatePlaylistDialog : PlaylistIntent
    data class OnConfirmCreatePlaylist(val name: String) : PlaylistIntent
    data class OnRemovePlaylist(val playlist: PlaylistEntity) : PlaylistIntent
    data class OnRenamePlaylistClick(val playlist: PlaylistEntity) : PlaylistIntent
    data object OnDismissRenamePlaylistDialog : PlaylistIntent
    data class OnConfirmRenamePlaylist(val newName: String) : PlaylistIntent
    data class OnPlaylistClick(val playlist: PlaylistEntity) : PlaylistIntent
}

sealed interface PlaylistEvent {
    data class NavigateToPlaylistDetail(val playlistId: String) : PlaylistEvent
}