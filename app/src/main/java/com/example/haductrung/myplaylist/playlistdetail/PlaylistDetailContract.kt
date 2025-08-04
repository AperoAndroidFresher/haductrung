package com.example.haductrung.myplaylist.playlistdetail


import com.example.haductrung.myplaylist.Playlist
import com.example.haductrung.library.minicomposable.Song

data class PlaylistDetailState(
    val playlist: Playlist? = null,
    val songs: List<Song> = emptyList(),
    val isGridView: Boolean = false,
    val isSortMode: Boolean = false,
    val songWithMenu: Int? = null,
    val isLoading: Boolean = true
)


sealed interface PlaylistDetailIntent {
    data class LoadPlaylistDetails(val playlistId: String) : PlaylistDetailIntent

    data object OnToggleViewClick : PlaylistDetailIntent
    data object OnToggleSortClick : PlaylistDetailIntent

    data class OnMoreClick(val song: Song) : PlaylistDetailIntent
    data object OnDismissMenu : PlaylistDetailIntent

    data class OnDeleteSongFromPlaylist(val song: Song) : PlaylistDetailIntent
}