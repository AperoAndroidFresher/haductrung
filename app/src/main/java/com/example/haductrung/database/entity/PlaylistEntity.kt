package com.example.haductrung.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["creator_user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,

    val name: String,

    @ColumnInfo(name = "creator_user_id", index = true)
    val creatorUserId: Int,

    val songIdsJson: String
)
