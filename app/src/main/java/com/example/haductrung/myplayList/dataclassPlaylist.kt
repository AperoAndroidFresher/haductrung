package com.example.haductrung.myplayList

import com.example.haductrung.library.minicomposable.Song

data class Playlist(
    val id: Int,
    val name: String,
    val songs: List<Song> = emptyList()
)
