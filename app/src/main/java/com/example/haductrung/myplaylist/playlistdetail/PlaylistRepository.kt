package com.example.haductrung.myplaylist.playlistdetail

import com.example.haductrung.myplaylist.Playlist



object PlaylistRepository {
    // Dùng Map tìm và cập nhật playlist bằng ID
    private val playlists = mutableMapOf<String, Playlist>()

    fun createPlaylist(playlist: Playlist) {
        playlists[playlist.id] = playlist
    }

    fun getPlaylists(): List<Playlist> {
        return playlists.values.toList()
    }

    fun findPlaylistById(id: String): Playlist? {
        return playlists[id]
    }

    //  cập nhật một playlist
    fun updatePlaylist(updatedPlaylist: Playlist) {
        // Ghi đè nếu tồn tại
        if (playlists.containsKey(updatedPlaylist.id)) {
            playlists[updatedPlaylist.id] = updatedPlaylist
        }
    }

    //  xóa
    fun removePlaylist(playlistId: String) {
        playlists.remove(playlistId)
    }
}