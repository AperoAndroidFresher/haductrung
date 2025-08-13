package com.example.haductrung.home

import android.net.Uri

data class HomeState(
    val isLoading: Boolean = false,
    val imageUri: Uri? = null,
    val username: String = ""
)


sealed interface HomeIntent {
    data object NavigateToProfile : HomeIntent
    data object HomeTabClicked : HomeIntent
    data object LibraryTabClicked : HomeIntent
    data object PlaylistTabClicked : HomeIntent
}


sealed interface HomeEvent {
    data object NavigateToProfile : HomeEvent
    data object NavigateToLibrary : HomeEvent
    data object NavigateToPlaylist : HomeEvent
}