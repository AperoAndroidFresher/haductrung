package com.example.haductrung.library

import com.example.haductrung.library.remote.RemoteSong
import com.example.haductrung.repository.Song

enum class LibraryTab {
    LOCAL, REMOTE
}
sealed class RemoteState {
    data object Loading : RemoteState()
    data class Success(val songs: List<Song>) : RemoteState()
    data class Error(val message: String) : RemoteState()
}
data class LibraryState(
    val songList: List<Song> = emptyList(),
    val isGridView: Boolean =false,
    val isSortMode: Boolean= false,
    val songWithMenu: Int?=null,
    val hasPermission: Boolean = false,

    val selectedTab: LibraryTab = LibraryTab.LOCAL,
    val remoteState: RemoteState = RemoteState.Loading
)

sealed interface LibraryIntent {
    data object OnToggleViewClick : LibraryIntent
    data object OnToggleSortClick : LibraryIntent
    data class OnMoreClick(val song: Song) :LibraryIntent
    data object OnDismissMenu :LibraryIntent
    data class OnAddToPlaylistClick(val song: Song) : LibraryIntent
    //data class OnDeleteClick(val song: Song) :LibraryIntent
    data object CheckAndLoadSongs : LibraryIntent
    data object OnRequestPermissionAgain: LibraryIntent

    data class OnTabSelected(val tab: LibraryTab) : LibraryIntent
    data object RetryFetchRemoteSongs : LibraryIntent
}
sealed interface LibraryEvent{
    data object RequestPermission : LibraryEvent
    data class NavigateToAddToPlaylistScreen(val song: Song) : LibraryEvent
}