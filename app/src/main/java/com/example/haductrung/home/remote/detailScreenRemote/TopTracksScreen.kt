package com.example.haductrung.home.remote.detailScreenRemote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.haductrung.home.TrackItem
import com.example.haductrung.home.BarColors
import com.example.haductrung.home.remote.ArtistInfo
import com.example.haductrung.home.remote.ImageInfo
import com.example.haductrung.home.remote.TrackFromApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopTracksScreen(
    tracks: List<TrackFromApi>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Top Tracks",
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
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            itemsIndexed(tracks) { index, track ->
                // Tính toán màu dựa trên index
                val color = BarColors[index % BarColors.size]
                TrackItem(track = track, color = color)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TopTracksScreenPreview() {
    val previewTracks = List(10) { index ->
        TrackFromApi(
            name = "Track Preview Title ${index + 1}",
            listeners = "12345",
            artist = ArtistInfo("Artist Name"),
            imageList = listOf(ImageInfo("", "extralarge"))
        )
    }
    TopTracksScreen(tracks = previewTracks, onBackClick = {})
}