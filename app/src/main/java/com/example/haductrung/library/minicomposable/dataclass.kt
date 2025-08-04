package com.example.haductrung.library.minicomposable

import android.net.Uri

data class Song(
    val id :Int,
    val title: String,
    val artist: String,
    val duration: String,
    val durationMs: Long,
    val filePath: String,
    val albumArtUri: Uri?
)