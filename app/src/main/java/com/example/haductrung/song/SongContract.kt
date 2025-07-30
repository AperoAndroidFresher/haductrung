package com.example.haductrung.song

import com.example.haductrung.song.minicomposable.Song


data class SongState(
    val songList: List<Song> = emptyList(),
    val isGridView: Boolean =false,
    val isSortMode: Boolean= false,
    val songWithMenu: Int?=null,
)

sealed interface SongIntent {
    data object onToggleViewClick : SongIntent
    data object onToggleSortClick : SongIntent
    data class onMoreClick(val song: Song) :SongIntent
    data object onDismissMenu :SongIntent
    data class onDeleteClick(val song: Song) :SongIntent
}
sealed interface SongEven{
}