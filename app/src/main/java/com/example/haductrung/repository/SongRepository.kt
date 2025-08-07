package com.example.haductrung.repository
import com.example.haductrung.database.DAO.SongDao
import com.example.haductrung.database.entity.SongEntity
import kotlinx.coroutines.flow.Flow
class SongRepository(private val songDao: SongDao) {

    fun getAllSongs(): Flow<List<SongEntity>> = songDao.getAllSongs()
    suspend fun insertAll(songs: List<SongEntity>) {
        songDao.insertAll(songs)
    }

    suspend fun insertSong(song: SongEntity): Long {
        return songDao.insert(song)
    }

    suspend fun findSongByFilePath(filePath: String): SongEntity? {
        return songDao.findByFilePath(filePath)
    }
    fun getSongsByIds(songIds: List<Int>): Flow<List<SongEntity>> {
        return songDao.getSongsByIds(songIds)
    }

}