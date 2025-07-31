package com.example.haductrung.song

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.R
import com.example.haductrung.signup_login.SignUpScreen.SignUpEvent
import com.example.haductrung.song.minicomposable.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.Manifest


class SongViewModel(
    private val repository: SongRepository,
    private val applicationContext: Context
    ) : ViewModel() {

    private val _state = MutableStateFlow(SongState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<SongEvent>()
    val event = _event.asSharedFlow()

    fun processIntent(intent: SongIntent) {
        when (intent) {
            is SongIntent.onToggleViewClick -> {
                _state.update { it.copy(isGridView = !it.isGridView) }
            }

            is SongIntent.onToggleSortClick -> {
                val currentState = _state.value
                val sortedList = if (!currentState.isSortMode) {
                    currentState.songList.sortedBy { it.title }
                } else {
                    currentState.songList
                }
                _state.update { it.copy(isSortMode = !it.isSortMode, songList = sortedList) }
            }

            is SongIntent.onMoreClick -> {
                _state.update { it.copy(songWithMenu = intent.song.id) }
            }

            is SongIntent.onDismissMenu -> {
                _state.update { it.copy(songWithMenu = null) }
            }

            is SongIntent.onDeleteClick -> {
                val newList = _state.value.songList.toMutableList().apply {
                    removeIf { it.id == intent.song.id }
                }
                _state.update { it.copy(songList = newList, songWithMenu = null) }
            }
            is SongIntent.CheckAndLoadSongs -> {
                checkPermissionAndLoad()
            }
            is SongIntent.onRequestPermissionAgain->{
                viewModelScope.launch { _event.emit(SongEvent.RequestPermission) }
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

            viewModelScope.launch { _event.emit(SongEvent.RequestPermission) }
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
