package com.example.haductrung.song


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.ui.theme.HaductrungTheme


@Composable
fun SongScreen(
    songList: List<Song>,
    isGridView: Boolean,
    isSortMode: Boolean,
    songWithMenu: Int?,

    onToggleViewClick: () -> Unit,
    onToggleSortClick: () -> Unit,
    onMoreClick: (Song) -> Unit,
    onDismissMenu: () -> Unit,
    onDeleteClick: (Song) -> Unit,
    onNavigateToProfile: () -> Unit
) {

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
                val iconResId = if (isGridView) R.drawable.bacham else R.drawable.grid
                val content = if (isGridView) "List View" else "Grid View"

                Image(
                    painter = painterResource(iconResId),
                    contentDescription = content,
                    modifier = Modifier
                        .size(35.dp)
                        .padding(end = 10.dp)
                        .clickable(onClick = onToggleViewClick)
                )

                val iconResID2 = if (isSortMode) R.drawable.tickv else R.drawable.sort
                val content2 = if (isSortMode) "Sorting" else "Normal"
                Image(
                    painter = painterResource(iconResID2),
                    contentDescription = content2,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(onClick = onToggleSortClick)

                )
            }
        }
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(songList.size) { index ->
                    val song1 = songList[index]
                    SongGridItem(
                        song = song1,
                        onMOPClick = { onMoreClick(song1) },
                        isMenuExpanded = (songWithMenu == song1.idanh),
                        onDismissMenu = onDismissMenu,
                        onDeleteClick = {
                            onDeleteClick(song1)
                        }

                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songList.size) { index ->
                    val song1 = songList[index]
                    SongItem(
                        song = song1,
                        onMOPClick = { onMoreClick(song1) },
                        isMenuExpanded = (songWithMenu == song1.idanh),
                        onDismissMenu = onDismissMenu,
                        onDeleteClick = {
                            onDeleteClick(song1)
                        },
                        isSort = isSortMode
                    )
                }
            }
        }
    }
}


