package com.example.haductrung.myplayList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import com.example.haductrung.R
import com.example.haductrung.myplayList.MyPlaylistIntent
import com.example.haductrung.myplayList.MyPlaylistState
import com.example.haductrung.myplayList.Playlist
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.tooling.preview.Preview
import com.example.haductrung.myplayList.minicomposable.PlaylistItem
import com.example.haductrung.ui.theme.HaductrungTheme

@Composable
fun MyPlaylistScreen(
    state: MyPlaylistState,
    onIntent: (MyPlaylistIntent) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        // --- Header ---
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                "My Playlists",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            // Icon "+" ở góc phải trên cùng
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Playlist",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {  }
            )
        }

        if (state.playlists.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "You don't have any playlists.\nClick the \"+\" button to add.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create new playlist",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, color =Color.Gray, RoundedCornerShape(16.dp))
                            .clickable { }
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.playlists) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        isMenuExpanded = (state.playlistIdWithMenu == playlist.id),
                        onDismissMenu = { onIntent(MyPlaylistIntent.DismissMenu) },
                        onDeleteClick = { onIntent(MyPlaylistIntent.DeletePlaylistClicked(playlist.id)) },
                        onMoreClick = { onIntent(MyPlaylistIntent.MoreOptionsClicked(playlist.id)) },
                        onPlaylistClick = { onIntent(MyPlaylistIntent.PlaylistClicked(playlist.id)) }
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewMyPlaylistScreen() {
    val sampleState = MyPlaylistState(
     playlists = listOf(
            Playlist(1, "Chill Vibes", emptyList()),
            Playlist(2, "Workout Mix", emptyList()),
            Playlist(3, "Late Night Drives", emptyList())
        )
    )

    HaductrungTheme {
        MyPlaylistScreen(
            state = sampleState,
            onIntent = {}
        )
    }
}