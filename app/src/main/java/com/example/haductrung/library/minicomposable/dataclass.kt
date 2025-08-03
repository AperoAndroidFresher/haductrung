package com.example.haductrung.library.minicomposable

import android.net.Uri

data class Song(
    val id :Int,
    val title: String,
    val artist: String,
    val duration: String,
    val albumArtUri: Uri?
)