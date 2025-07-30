package com.example.haductrung.song

import androidx.lifecycle.ViewModel
import com.example.haductrung.R
import com.example.haductrung.song.minicomposable.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SongViewModel : ViewModel() {

    private val _state = MutableStateFlow(SongState())
    val state = _state.asStateFlow()

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

        }
    }

    init {
        loadSongs()
    }

    private fun loadSongs() {

        val initialSongs = listOf(
            Song(1, "Rainy days", "Moody,", "04:30", R.drawable.grainydays),
            Song(2, "Cofffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song(3, "Rainfdrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song(4, "Tokydo", "SmYang", "04:02", R.drawable.tokyo),
            Song(5, "Lulflaby", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song(6, "Raindsy dayss", "Moody,", "04:30", R.drawable.grainydays),
            Song(7, "Rainy days", "Moody,", "04:30", R.drawable.grainydays),
            Song(8, "Cofffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song(9, "Rainfdrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song(10, "Tokydo", "SmYang", "04:02", R.drawable.tokyo),
            Song(11, "Lulflaby", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song(12, "Raindsy dayss", "Moody,", "04:30", R.drawable.grainydays),
            Song(13, "Rainy days", "Moody,", "04:30", R.drawable.grainydays),
            Song(14, "Cofffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song(15, "Rainfdrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song(16, "Tokydo", "SmYang", "04:02", R.drawable.tokyo),
            Song(17, "Lulflaby", "Iamfinenow", "04:02", R.drawable.lulabby)
        )
        _state.update { it.copy(songList = initialSongs) }
    }
}