package com.example.haductrung.library

import com.example.haductrung.repository.Song


data class LibraryState(
    val songList: List<Song> = emptyList(),
    val isGridView: Boolean =false,
    val isSortMode: Boolean= false,
    val songWithMenu: Int?=null,
    val hasPermission: Boolean = false
)

sealed interface LibraryIntent {
    data object OnToggleViewClick : LibraryIntent
    data object OnToggleSortClick : LibraryIntent
    data class OnMoreClick(val song: Song) :LibraryIntent
    data object OnDismissMenu :LibraryIntent
    data class OnAddToPlaylistClick(val song: Song) : LibraryIntent
    data class OnDeleteClick(val song: Song) :LibraryIntent
    data object CheckAndLoadSongs : LibraryIntent
    data object OnRequestPermissionAgain: LibraryIntent
}
sealed interface LibraryEvent{
    data object RequestPermission : LibraryEvent
    data class NavigateToAddToPlaylistScreen(val song: Song) : LibraryEvent
}