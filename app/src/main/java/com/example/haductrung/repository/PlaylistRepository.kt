package com.example.haductrung.repository

import com.example.haductrung.database.dao.PlaylistDao
import com.example.haductrung.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow


class PlaylistRepository(private val playlistDao: PlaylistDao) {
    fun getPlayListForUser(userId:Int):Flow<List<PlaylistEntity>>{
        return playlistDao.getPlaylistsForUser(userId)
    }
    suspend fun createPlaylist(playlist: PlaylistEntity){
         playlistDao.insert(playlist)
    }
    suspend fun updatePlaylist(playlist: PlaylistEntity){
        playlistDao.update(playlist)
    }
    suspend fun deletePlaylist(playlist: PlaylistEntity){
        playlistDao.delete(playlist)
    }
    suspend fun findPlaylistById(id: Int): Flow<PlaylistEntity?> {
        return playlistDao.findPlaylistById(id)
    }
}