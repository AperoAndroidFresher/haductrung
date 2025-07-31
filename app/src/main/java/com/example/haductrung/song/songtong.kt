package com.example.haductrung.song


import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.song.minicomposable.SongGridItem
import com.example.haductrung.song.minicomposable.SongItem


@Composable
fun SongScreen(
    state: SongState,
    onIntent:(SongIntent)->Unit,
) {
    val activity = (LocalActivity.current)
    BackHandler {
        activity?.finish()
    }
    if(state.hasPermission) {
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
                    "My Playlist",
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
                            .clickable { onIntent(SongIntent.onToggleViewClick) }
                    )

                    val iconResID2 = if (state.isSortMode) R.drawable.tickv else R.drawable.sort
                    val content2 = if (state.isSortMode) "Sorting" else "Normal"
                    Image(
                        painter = painterResource(iconResID2),
                        contentDescription = content2,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onIntent(SongIntent.onToggleSortClick) }

                    )
                }
            }
            if (state.isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.songList.size) { index ->
                        val song1 = state.songList[index]
                        SongGridItem(
                            song = song1,
                            onMoreClick = { onIntent(SongIntent.onMoreClick(song1)) },
                            isMenuExpanded = (state.songWithMenu == song1.id),
                            onDismissMenu = { onIntent(SongIntent.onDismissMenu) },
                            onDeleteClick = {
                                onIntent(SongIntent.onDeleteClick(song1))
                            }

                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.songList.size) { index ->
                        val song1 = state.songList[index]
                        SongItem(
                            song = song1,
                            onMoreClick = { onIntent(SongIntent.onMoreClick(song1)) },
                            isMenuExpanded = (state.songWithMenu == song1.id),
                            onDismissMenu = { onIntent(SongIntent.onDismissMenu) },
                            onDeleteClick = {
                                onIntent(SongIntent.onDeleteClick(song1))
                            },
                            isSortMode = state.isSortMode
                        )
                    }
                }
            }
        }
    }else{
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onIntent(SongIntent.onRequestPermissionAgain) }
                ) {
                    Text("Please grant access permission in settings", color = Color.White)
                }

        }
    }
}

