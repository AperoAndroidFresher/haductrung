package com.example.haductrung.home.remote.detailScreenRemote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.haductrung.home.AlbumItem
import com.example.haductrung.home.remote.AlbumFromApi
import com.example.haductrung.home.remote.ArtistInfo
import com.example.haductrung.home.remote.ImageInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAlbumsScreen(
    albums: List<AlbumFromApi>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Top Albums",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(albums) { album ->
                AlbumItem(album = album)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun TopAlbumsScreenPreview() {
    val previewAlbums = List(10) { index ->
        AlbumFromApi(
            name = "Album Preview ${index + 1}",
            artist = ArtistInfo("Artist Name"),
            imageList = listOf(ImageInfo("", "extralarge"))
        )
    }
    TopAlbumsScreen(albums = previewAlbums, onBackClick = {})
}