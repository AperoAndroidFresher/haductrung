package com.example.haductrung.lesson9_songs


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

data class Song(
    val title: String,
    val artist: String,
    val duration: String,
    val idanh: Int
)


@Composable
fun DeleteSong(onDeleteClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .clickable(onClick = onDeleteClick)
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.remove),
                contentDescription = "xoa",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Remove from playlist",
                color = Color.White,
                fontSize = 15.sp
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.share),
                contentDescription = "Share",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Share (coming soon)",
                color = Color.Gray,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    modifier: Modifier = Modifier,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onMOPClick: () -> Unit,
    isSort: Boolean

) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(song.idanh),
            song.title,
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                song.artist,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                song.duration,
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Box {
                val iconResId3 = if (isSort) R.drawable.hbg else R.drawable.bacham
                val contenDes3 = if (isSort) "sortting" else "normal"
                Image(
                    painter = painterResource(iconResId3),
                    contentDescription = contenDes3,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (!isSort) {
                                onMOPClick()
                            }
                        }
                )
                if(!isSort) {
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = onDismissMenu,
                        modifier = Modifier
                            .background(color = Color.Black)
                            .clip(RoundedCornerShape(16.dp)),
                    ) {
                        DeleteSong(onDeleteClick = onDeleteClick)
                    }
                }
            }
        }
    }
}

@Composable
fun SongGridItem(
    song: Song,
    onMOPClick: () -> Unit,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onDeleteClick: () -> Unit,


    ) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.grainydays),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Image(
                    painter = painterResource(id = R.drawable.bacham),
                    contentDescription = "moreoption",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .align(alignment = Alignment.TopEnd)
                        .clickable(onClick = onMOPClick),
                )
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onDismissMenu,
                    modifier = Modifier
                        .background(color = Color.Black)
                        .clip(RoundedCornerShape(16.dp)),
                ) {
                    DeleteSong(onDeleteClick = onDeleteClick)
                }

            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = song.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            maxLines = 1
        )
        Text(
            text = song.artist,
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = song.duration,
            color = Color.LightGray,
            fontSize = 20.sp
        )
    }
}

@Composable
fun SongScreen() {
    val songList = remember {
        mutableListOf(
            Song("Rainy days", "Moody,", "04:30", R.drawable.grainydays),
            Song("Coffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song("Raindrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song("Tokyo", "SmYang", "04:02", R.drawable.tokyo),
            Song("Lullaby", "Iamfinenow", "04:02", R.drawable.list),
            Song("Rainy dayss", "Moody,", "04:30", R.drawable.grainydays),
            Song("Coffeee", "Kainbeats", "04:30", R.drawable.cofee),
            Song("Raindropss", "Rainyyxx", "00:30", R.drawable.remove),
            Song("Tokyoo", "SmYang", "04:02", R.drawable.tokyo),
            Song("Lullabyuy", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song("Rainy daysđ", "Moody,", "04:30", R.drawable.grainydays),
            Song("Coffeefd", "Kainbeats", "04:30", R.drawable.cofee),
            Song("Raindropfds", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song("Tokyhho", "SmYang", "04:02", R.drawable.tokyo),
            Song("Lullahhby", "Iamfinenow", "04:02", R.drawable.list),

            )
    }
    var isGridView by remember { mutableStateOf(false) }
    var songWithMenu by remember { mutableStateOf<Song?>(null) }
    var isSort by remember { mutableStateOf(false) }
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
                val iconResId = if (isGridView) R.drawable.list else R.drawable.grid
                val content = if (isGridView) "List View" else "Grid View"

                Image(
                    painter = painterResource(iconResId),
                    contentDescription = content,
                    modifier = Modifier
                        .size(35.dp)
                        .padding(end = 10.dp)
                        .clickable { isGridView = !isGridView }
                )

                val iconResID2 = if (isSort) R.drawable.tickv else R.drawable.sort1
                val content2 = if (isSort) "Sorting" else "Normal"
                Image(
                    painter = painterResource(iconResID2),
                    contentDescription = content2,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            isSort=!isSort
                        }

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
                        onMOPClick = { songWithMenu = song1 },
                        isMenuExpanded = (songWithMenu == song1),
                        onDismissMenu = { songWithMenu = null },
                        onDeleteClick = {
                            songList.remove(song1)
                            songWithMenu = null
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
                        onMOPClick = { songWithMenu = song1 },
                        isMenuExpanded = (songWithMenu == song1),
                        onDismissMenu = { songWithMenu = null },
                        onDeleteClick = {
                            songList.remove(song1)
                            songWithMenu = null
                        },
                        isSort = isSort
                    )
                }
            }
        }

    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewSongScreen() {
    //val ss = Song("Rainy days", "Moody,", "04:30", R.drawable.grainydays)
    HaductrungTheme {
        SongScreen()
        // SongGridItem(song = ss)
        //DeleteSong(onDeleteClick = {})
    }
}
