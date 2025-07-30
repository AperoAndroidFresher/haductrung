package com.example.haductrung.library



data class LibraryState(
    val title: String = "Library Screen"
)


sealed interface LibraryIntent {
}

sealed interface LibraryEvent {

}