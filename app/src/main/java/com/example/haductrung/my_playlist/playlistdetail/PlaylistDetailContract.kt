package com.example.haductrung.my_playlist.playlistdetail


import com.example.haductrung.database.entity.PlaylistEntity
import com.example.haductrung.my_playlist.Playlist
import com.example.haductrung.library.Song

data class PlaylistDetailState(
    val playlist: PlaylistEntity? = null,
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