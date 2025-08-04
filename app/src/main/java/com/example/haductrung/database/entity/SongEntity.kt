package com.example.haductrung.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val songId: Int = 0,

    val title: String,
    val artist: String,
    val durationMs: Long,
    val filePath: String,
    val albumArtUri: String?
)