package com.example.haductrung.library.minicomposable.addtoplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.database.Converters
import com.example.haductrung.database.entity.PlaylistEntity
import com.example.haductrung.my_playlist.playlistdetail.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddToPlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddToPlaylistState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddToPlaylistEvent>()
    val event = _event.asSharedFlow()

    init {
        val songId: Int = savedStateHandle["songId"] ?: -1
        processIntent(AddToPlaylistIntent.LoadPlaylists(songId))
    }

    fun processIntent(intent: AddToPlaylistIntent) {
        when (intent) {
            is AddToPlaylistIntent.LoadPlaylists -> {
                _state.update { it.copy(isLoading = true, songIdToAdd = intent.songId) }
                // TODO: Lấy userId của người dùng đang đăng nhập
                val currentUserId = 1 // Tạm thời dùng ID = 1
                viewModelScope.launch {
                    // Lắng nghe danh sách playlist từ database
                    playlistRepository.getPlayListForUser(currentUserId).collect { playlists ->
                        _state.update { it.copy(isLoading = false, playlists = playlists) }
                    }
                }
            }

            is AddToPlaylistIntent.OnPlaylistSelected -> {
                // Chạy trên luồng nền để thao tác với DB
                viewModelScope.launch(Dispatchers.IO) {
                    addSongToPlaylist(intent.playlist)
                }
            }

            is AddToPlaylistIntent.NavigateToMyPlaylistTab -> {
                viewModelScope.launch {
                    _event.emit(AddToPlaylistEvent.NavigateToMyPlaylist)
                }
            }
        }
    }

    private suspend fun addSongToPlaylist(selectedPlaylist: PlaylistEntity) {
        val songId = state.value.songIdToAdd
        if (songId != null && songId != -1) {
            val converters = Converters()
            // 1. Dùng TypeConverter để đọc chuỗi JSON thành List<Int>
            val currentSongIds = converters.fromString(selectedPlaylist.songIdsJson).toMutableList()

            // 2. Chỉ thêm nếu ID chưa tồn tại
            if (!currentSongIds.contains(songId)) {
                currentSongIds.add(songId)

                // 3. Dùng TypeConverter để chuyển List ngược lại thành chuỗi JSON
                val updatedSongIdsJson = converters.fromIntList(currentSongIds)

                // 4. Tạo object playlist mới và cập nhật vào database
                val updatedPlaylist = selectedPlaylist.copy(songIdsJson = updatedSongIdsJson)
                playlistRepository.updatePlaylist(updatedPlaylist)

                // 5. Gửi sự kiện thành công
                _event.emit(AddToPlaylistEvent.ShowSuccessMessage(updatedPlaylist.name))
            }
            // Luôn đóng màn hình lại sau khi chọn
            _event.emit(AddToPlaylistEvent.GoBack)
        }
    }
}