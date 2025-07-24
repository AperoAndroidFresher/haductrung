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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
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
    val coverResId: Int
)

@Composable
fun SongGridItem(song: Song) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {
            Image(
                painter = painterResource(id = song.coverResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Image(
                painter = painterResource(id = R.drawable.bacham),
                contentDescription = "moreoption",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .align(alignment = Alignment.TopEnd).clickable { },

            )
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
fun DeleteSong(onDeleteClick : ()->Unit) {
    Column(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color = Color.Black))  {
        Row(modifier = Modifier.clickable (onClick = onDeleteClick)) {
            Image(
                painter = painterResource(R.drawable.remove),
                contentDescription = "xoa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape).padding(2.dp).padding(start = 2.dp, end = 2.dp),
            )

            Text(
                text = "Remove from playlist",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end= 3.dp)

            )
        }
        Row(modifier = Modifier) {
            Image(
                painter = painterResource(R.drawable.share),
                contentDescription = "xoa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape).padding(2.dp).padding(start = 2.dp, end = 2.dp),
            )

            Text(
                text = "Share (coming soon)",
                color = Color.LightGray,
                fontSize = 10.sp,
                modifier = Modifier.padding(end= 3.dp)

            )
        }
    }

}

@Composable
fun SongItem(
    song: Song,
    modifier: Modifier = Modifier,
    //onDeleteRequest: (Song)->Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(song.coverResId),
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
            Image(
                painter = painterResource(R.drawable.bacham),
                contentDescription = "more option",
                modifier =Modifier.size(20.dp)

            )
        }
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
            Song("Lullaby", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song("Coffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song("Raindrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song("Tokyo", "SmYang", "04:02", R.drawable.tokyo),
            Song("Lullaby", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song("Coffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song("Raindrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song("Tokyo", "SmYang", "04:02", R.drawable.tokyo),
            Song("Lullaby", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song("Coffee", "Kainbeats", "04:30", R.drawable.cofee),
            Song("Raindrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song("Tokyo", "SmYang", "04:02", R.drawable.tokyo),
            Song("Lullaby", "Iamfinenow", "04:02", R.drawable.lulabby)
        )
    }
    var isGridView by remember { mutableStateOf(false) }
    var isdelete by remember{ mutableStateOf<Song?>(null) }

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


                Image(
                    painter = painterResource(R.drawable.sort1),
                    contentDescription = "Sort",
                    modifier = Modifier
                        .size(30.dp)

                )
            }
        }
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(songList.size) { index ->
                    SongGridItem(song = songList[index])
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songList.size) { index ->
                    SongItem(song = songList[index])
                }
            }
        }

    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewSongScreen() {
    val ss = Song("Rainy days", "Moody,", "04:30", R.drawable.grainydays)
    HaductrungTheme {
         SongScreen()
        // SongGridItem(song = ss)
        //DeleteSong()
    }
}

