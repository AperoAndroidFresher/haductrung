package com.example.haductrung.library

import com.example.haductrung.library.minicomposable.Song


data class LibraryState(
    val songList: List<Song> = emptyList(),
    val isGridView: Boolean =false,
    val isSortMode: Boolean= false,
    val songWithMenu: Int?=null,
    val hasPermission: Boolean = false
)

sealed interface LibraryIntent {
    data object onToggleViewClick : LibraryIntent
    data object onToggleSortClick : LibraryIntent
    data class onMoreClick(val song: Song) :LibraryIntent
    data object onDismissMenu :LibraryIntent
    data class onAddplaylistClick(val song: Song) :LibraryIntent
    data object CheckAndLoadSongs : LibraryIntent
    data object onRequestPermissionAgain: LibraryIntent
}
sealed interface LibraryEvent{
    data class ShowAddToPlaylistDialog(val song: Song) : LibraryEvent
    data object RequestPermission : LibraryEvent
}