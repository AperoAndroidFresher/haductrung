package com.example.haductrung.database.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.haductrung.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insert(playlist: PlaylistEntity)

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Delete
    suspend fun delete(playlist: PlaylistEntity)

    @Query("""
        SELECT * 
        FROM playlists 
        WHERE playlistId = :id 
        LIMIT 1
    """)
    fun findPlaylistById(id: Int): Flow<PlaylistEntity?>
    //lấy playlist by userid
    @Query("""
        SELECT * 
        FROM playlists 
        WHERE creator_user_id = :userId 
        ORDER BY name ASC
    """)
    fun getPlaylistsForUser(userId: Int): Flow<List<PlaylistEntity>>
}