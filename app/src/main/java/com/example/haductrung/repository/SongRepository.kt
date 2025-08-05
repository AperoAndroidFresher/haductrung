package com.example.haductrung.repository
import com.example.haductrung.database.DAO.SongDao
import com.example.haductrung.database.entity.SongEntity
import kotlinx.coroutines.flow.Flow
class SongRepository(private val songDao: SongDao) {

    fun getAllSongs(): Flow<List<SongEntity>> = songDao.getAllSongs()
    suspend fun insertAll(songs: List<SongEntity>) {
        songDao.insertAll(songs)
    }
    fun getSongsByIds(songIds: List<Int>): Flow<List<SongEntity>> {
        return songDao.getSongsByIds(songIds)
    }

}