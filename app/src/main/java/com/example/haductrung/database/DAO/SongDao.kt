package com.example.haductrung.database.DAO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.haductrung.database.entity.SongEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(song: SongEntity): Long
    @Query("""
        SELECT * 
        FROM songs 
        ORDER BY title ASC
    """)
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("""
        SELECT * 
        FROM songs 
        WHERE filePath = :filePath 
        LIMIT 1
    """)
    suspend fun findByFilePath(filePath: String): SongEntity?


    @Query("""
        SELECT * 
        FROM songs 
        WHERE songId IN (:songIds)
    """)
    fun getSongsByIds(songIds: List<Int>): Flow<List<SongEntity>>
}