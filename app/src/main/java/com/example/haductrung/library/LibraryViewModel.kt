package com.example.haductrung.library

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.Manifest


class LibraryViewModel(
    private val repository: LibraryRepository,
    private val applicationContext: Context
    ) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<LibraryEvent>()
    val event = _event.asSharedFlow()

    fun processIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.onToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }

            is LibraryIntent.onToggleSortClick -> {
                val currentState = _state.value
                val sortedList = if (!currentState.isSortMode) {
                    currentState.songList.sortedBy { it.title }
                } else {
                    currentState.songList
                }
                _state.update { it.copy(isSortMode = !it.isSortMode, songList = sortedList) }
            }

            is LibraryIntent.onMoreClick -> {
                _state.update { it.copy(songWithMenu = intent.song.id) }
            }

            is LibraryIntent.onDismissMenu -> {
                _state.update { it.copy(songWithMenu = null) }
            }

            is LibraryIntent.onAddplaylistClick -> {
                viewModelScope.launch {
                    _event.emit(LibraryEvent.ShowAddToPlaylistDialog(intent.song))
                }
            }
            is LibraryIntent.CheckAndLoadSongs -> {
                checkPermissionAndLoad()
            }
            is LibraryIntent.onRequestPermissionAgain->{
                viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
            }

        }
    }
    private fun checkPermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isGranted = applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

        _state.update { it.copy(hasPermission = isGranted) }

        if (isGranted) {
            loadSongs()
        } else {

            viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
        }
    }
    fun loadSongs() {
        if (state.value.songList.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val songs = repository.getAudioData()
            withContext(Dispatchers.Main) {
                _state.update { it.copy(songList = songs) }
            }

        }
    }
}
