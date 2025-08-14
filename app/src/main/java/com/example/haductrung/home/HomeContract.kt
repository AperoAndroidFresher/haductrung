package com.example.haductrung.home

import android.net.Uri
import com.example.haductrung.home.remote.AlbumFromApi
import com.example.haductrung.home.remote.ArtistFromApi
import com.example.haductrung.home.remote.TrackFromApi

sealed interface ContentLoadingState {
    object Loading : ContentLoadingState
    object Success : ContentLoadingState
    object Error : ContentLoadingState
}
data class HomeState(
    val isLoading: Boolean = false,
    val imageUri: Uri? = null,
    val username: String = "",

    val topAlbums: List<AlbumFromApi> = emptyList(),
    val topAlbumsState: ContentLoadingState = ContentLoadingState.Loading,

    val topTracks: List<TrackFromApi> = emptyList(),
    val topTracksState: ContentLoadingState = ContentLoadingState.Loading,

    val topArtists: List<ArtistFromApi> = emptyList(),
    val topArtistsState: ContentLoadingState = ContentLoadingState.Loading
)

sealed interface HomeIntent {
    data object NavigateToProfile : HomeIntent
    data object HomeTabClicked : HomeIntent
    data object LibraryTabClicked : HomeIntent
    data object PlaylistTabClicked : HomeIntent
    data object RetryFetchAll  : HomeIntent
    data object NavigateToTopAlbums : HomeIntent
    data object NavigateToTopArtists : HomeIntent
    data object NavigateToTopTracks : HomeIntent
    data object NavigateToSettings : HomeIntent
}


sealed interface HomeEvent {
    data object NavigateToProfile : HomeEvent
    data object NavigateToLibrary : HomeEvent
    data object NavigateToPlaylist : HomeEvent
    data object NavigateToTopAlbums : HomeEvent
    data object NavigateToTopArtists : HomeEvent
    data object NavigateToTopTracks : HomeEvent
    data object NavigateToSettings : HomeEvent
}