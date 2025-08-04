package com.example.haductrung.my_playlist.playlistdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.library.Song
import com.example.haductrung.library.minicomposable.CustomMenuItem
import com.example.haductrung.library.minicomposable.SongGridItem
import com.example.haductrung.library.minicomposable.SongItem


@Composable
fun PlaylistDetailScreen(
    state: PlaylistDetailState,
    onIntent: (PlaylistDetailIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PlaylistDetailTopBar(
            playlistName = state.playlist?.name ?: "Playlist",
            isGridView = state.isGridView,
            onIntent = onIntent
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "This playlist is empty.", color = Color.Gray)
            }
        } else {
            if (state.isGridView) {
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.songs,
                        key = { song -> song.id }
                    ) { song ->
                        SongGridItem (
                            song = song,
                            onMoreClick = { onIntent(PlaylistDetailIntent.OnMoreClick(song)) },
                            isMenuExpanded = state.songWithMenu == song.id,
                            onDismissMenu = { onIntent(PlaylistDetailIntent.OnDismissMenu) },
                            menuContent = {
                                CustomMenuItem(
                                    text = "Remove from playlist",
                                    iconResId = R.drawable.remove,
                                    onClick = {
                                        onIntent(PlaylistDetailIntent.OnDeleteSongFromPlaylist(song))
                                        onIntent(PlaylistDetailIntent.OnDismissMenu)
                                    }
                                )
                                Divider(color = Color.Gray.copy(alpha = 0.2f))
                                CustomMenuItem(
                                    text = "Share",
                                    iconResId = R.drawable.share,
                                    onClick = {
                                        onIntent(PlaylistDetailIntent.OnDismissMenu)
                                    }
                                )
                            }
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.songs,
                        key = { song -> song.id }
                    ) { song ->
                        SongItem(
                            song = song,
                            isMenuExpanded = state.songWithMenu == song.id,
                            onDismissMenu = { onIntent(PlaylistDetailIntent.OnDismissMenu) },
                            onMoreClick = { onIntent(PlaylistDetailIntent.OnMoreClick(song)) },
                            isSortMode = state.isSortMode,
                            menuContent = {
                                CustomMenuItem(
                                    text = "Remove from playlist",
                                    iconResId = R.drawable.remove,
                                    onClick = {
                                        onIntent(PlaylistDetailIntent.OnDeleteSongFromPlaylist(song))
                                        onIntent(PlaylistDetailIntent.OnDismissMenu)
                                    }
                                )
                                Divider(color = Color.Gray.copy(alpha = 0.2f))
                                CustomMenuItem(
                                    text = "Share",
                                    iconResId = R.drawable.share,
                                    onClick = {
                                        onIntent(PlaylistDetailIntent.OnDismissMenu)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistDetailTopBar(
    playlistName: String,
    isGridView: Boolean,
    onIntent: (PlaylistDetailIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = playlistName,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        val viewIcon = if (isGridView) R.drawable.list else R.drawable.grid
        Image(
            painter = painterResource(id = viewIcon),
            contentDescription = "Toggle View",
            modifier = Modifier
                .size(30.dp)
                .clickable { onIntent(PlaylistDetailIntent.OnToggleViewClick) }
        )
    }
}

// Thêm các Preview vào đây để dễ kiểm tra giao diện
@Preview(name = "Loaded - List View", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PlaylistDetailScreenLoadedListPreview() {
    val sampleSongs = listOf(
        Song(1, "Blinding Lights", "The Weeknd", "3:20", 200000, "", null),
        Song(2, "As It Was", "Harry Styles", "2:47", 167000, "", null)
    )
    PlaylistDetailScreen(
        state = PlaylistDetailState(
            isLoading = false,
            songs = sampleSongs
        ),
        onIntent = {}
    )
}