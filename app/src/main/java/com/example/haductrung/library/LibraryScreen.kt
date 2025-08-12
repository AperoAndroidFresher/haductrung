package com.example.haductrung.library


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.library.minicomposable.CustomMenuItem
import com.example.haductrung.library.minicomposable.LibraryTabs
import com.example.haductrung.library.minicomposable.SongGridItem
import com.example.haductrung.library.minicomposable.SongItem
import com.example.haductrung.musicPlayerBar.PlayerUiIntent
import com.example.haductrung.playback.PlayerManager
import com.example.haductrung.repository.Song


@Composable
fun LibraryScreen(
    state: LibraryState,
    onIntent: (LibraryIntent) -> Unit,
    onPlaySong: (Song) -> Unit
) {
//    val activity = (LocalActivity.current)
//    BackHandler {
//        activity?.finish()
//    }
    if (state.hasPermission) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp)
        ) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    "Library",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                Row(
                    Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconResId = if (state.isGridView) R.drawable.list else R.drawable.grid
                    val content = if (state.isGridView) "List View" else "Grid View"

                    Image(
                        painter = painterResource(iconResId),
                        contentDescription = content,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 10.dp)
                            .clickable { onIntent(LibraryIntent.OnToggleViewClick) }
                    )

                    val iconResID2 = if (state.isSortMode) R.drawable.tickv else R.drawable.sort
                    val content2 = if (state.isSortMode) "Sorting" else "Normal"
                    Image(
                        painter = painterResource(iconResID2),
                        contentDescription = content2,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onIntent(LibraryIntent.OnToggleSortClick) }

                    )
                }
            }
            LibraryTabs(
                selectedTab = state.selectedTab,
                onTabSelected = { selectedTab ->
                    onIntent(LibraryIntent.OnTabSelected(selectedTab))
                }
            )
            when (state.selectedTab) {
                LibraryTab.LOCAL -> {
                    if (state.isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(state.songList.size) { index ->
                                val song1 = state.songList[index]
                                val isSelected = song1.id == state.selectedSongId
                                SongGridItem(
                                    song = song1,
                                    isSelected = isSelected,
                                    onClick = {
                                        onIntent(LibraryIntent.OnSongSelected(song1.id))
                                        PlayerManager.viewModel.processIntent(PlayerUiIntent.PlaySong(song1))
                                    },
                                    onMoreClick = { onIntent(LibraryIntent.OnMoreClick(song1)) },
                                    isMenuExpanded = (state.songWithMenu == song1.id),
                                    onDismissMenu = { onIntent(LibraryIntent.OnDismissMenu) },
                                    menuContent = {

                                        CustomMenuItem(
                                            text = "Add to playlist",
                                            iconResId = R.drawable.addplaylist,
                                            onClick = {
                                                onIntent(LibraryIntent.OnAddToPlaylistClick(song1))
                                                onIntent(LibraryIntent.OnDismissMenu)
                                            }
                                        )
                                        CustomMenuItem(
                                            text = "Share",
                                            iconResId = R.drawable.share,
                                            onClick = { onIntent(LibraryIntent.OnDismissMenu) }
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(state.songList.size) { index ->
                                val song1 = state.songList[index]
                                val isSelected = song1.id == state.selectedSongId
                                SongItem(
                                    song = song1,
                                    isSelected = isSelected,
                                    onClick = {
                                        onIntent(LibraryIntent.OnSongSelected(song1.id))
                                        PlayerManager.viewModel.processIntent(PlayerUiIntent.PlaySong(song1))
                                    },
                                    onMoreClick = { onIntent(LibraryIntent.OnMoreClick(song1)) },
                                    isMenuExpanded = (state.songWithMenu == song1.id),
                                    onDismissMenu = { onIntent(LibraryIntent.OnDismissMenu) },
                                    isSortMode = state.isSortMode,
                                    menuContent = {
                                        CustomMenuItem(
                                            text = "Add to playlist",
                                            iconResId = R.drawable.addplaylist,
                                            onClick = {
                                                onIntent(LibraryIntent.OnAddToPlaylistClick(song1))
                                                onIntent(LibraryIntent.OnDismissMenu)
                                            }
                                        )
                                        CustomMenuItem(
                                            text = "Share",
                                            iconResId = R.drawable.share,
                                            onClick = { onIntent(LibraryIntent.OnDismissMenu) }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                LibraryTab.REMOTE -> {
                    when (val remoteState = state.remoteState) {
                        is RemoteState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is RemoteState.Success -> {
                            if (state.isGridView) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 100.dp)
                                ) {
                                    items(remoteState.songs) { song ->
                                        val isSelected = song.id == state.selectedSongId
                                        SongGridItem(
                                            song = song,
                                            isSelected = isSelected,
                                            onClick = {
                                                onIntent(LibraryIntent.OnSongSelected(song.id))
                                                PlayerManager.viewModel.processIntent(PlayerUiIntent.PlaySong(song))
                                            },
                                            onMoreClick = { onIntent(LibraryIntent.OnMoreClick(song)) },
                                            isMenuExpanded = (state.songWithMenu == song.id),
                                            onDismissMenu = { onIntent(LibraryIntent.OnDismissMenu) },
                                            menuContent = {
                                                CustomMenuItem(
                                                    text = "Add to playlist",
                                                    iconResId = R.drawable.addplaylist,
                                                    onClick = {
                                                        onIntent(LibraryIntent.OnAddToPlaylistClick(song))
                                                        onIntent(LibraryIntent.OnDismissMenu)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 100.dp)
                                ) {
                                    items(remoteState.songs) { song ->
                                        val isSelected = song.id == state.selectedSongId
                                        SongItem(
                                            song = song,
                                            isSortMode = false,
                                            isSelected = isSelected,
                                            onClick = {
                                                onIntent(LibraryIntent.OnSongSelected(song.id))
                                                PlayerManager.viewModel.processIntent(PlayerUiIntent.PlaySong(song))
                                            },
                                            isMenuExpanded = (state.songWithMenu == song.id),
                                            onDismissMenu = { onIntent(LibraryIntent.OnDismissMenu) },
                                            onMoreClick = { onIntent(LibraryIntent.OnMoreClick(song)) },
                                            menuContent = {

                                                CustomMenuItem(
                                                    text = "Add to playlist",
                                                    iconResId = R.drawable.addplaylist,
                                                    onClick = {
                                                        onIntent(LibraryIntent.OnAddToPlaylistClick(song))
                                                        onIntent(LibraryIntent.OnDismissMenu)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        is RemoteState.Error -> {
                            ErrorView(onIntent = onIntent)
                        }
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onIntent(LibraryIntent.OnRequestPermissionAgain) }
            ) {
                Text("Please grant access permission in settings", color = Color.White)
            }

        }
    }
}
@Composable
private fun ErrorView(onIntent: (LibraryIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 80.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_no_internet),
            contentDescription = "No Internet",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No internet connection,\nplease check your\n connection again",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onIntent(LibraryIntent.RetryFetchRemoteSongs) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C2CB)),
            shape = RoundedCornerShape(16)
        ) {
            Text(text = "Try again", color = Color.White)
        }
    }
}





