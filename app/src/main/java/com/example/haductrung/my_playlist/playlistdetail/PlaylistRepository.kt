package com.example.haductrung.my_playlist.playlistdetail

import com.example.haductrung.database.DAO.PlaylistDao
import com.example.haductrung.database.entity.PlaylistEntity
import com.example.haductrung.my_playlist.Playlist
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
    suspend fun findPlaylistById(id: Int): PlaylistEntity? {
        return playlistDao.findPlaylistById(id)
    }
}