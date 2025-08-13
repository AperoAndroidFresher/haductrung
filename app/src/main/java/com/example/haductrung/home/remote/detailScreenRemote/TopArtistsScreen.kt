package com.example.haductrung.home.remote.detailScreenRemote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.haductrung.home.ArtistItem
import com.example.haductrung.home.remote.ArtistFromApi
import com.example.haductrung.home.remote.ImageInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopArtistsScreen(
    artists: List<ArtistFromApi>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Top Artists",
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(artists) { artist ->
                ArtistItem(artist = artist)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopArtistsScreenPreview() {
    val previewArtists = List(10) { index ->
        ArtistFromApi(
            name = "Artist ${index + 1}",
            imageList = listOf(ImageInfo("", "extralarge"))
        )
    }
    TopArtistsScreen(artists = previewArtists, onBackClick = {})
}