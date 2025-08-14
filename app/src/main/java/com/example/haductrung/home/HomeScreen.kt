package com.example.haductrung.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.haductrung.R
import com.example.haductrung.home.remote.AlbumFromApi
import com.example.haductrung.home.remote.ArtistFromApi
import com.example.haductrung.home.remote.TrackFromApi
import com.example.haductrung.profile.minicomposable.CircularProfileImage
val BarColors = listOf(
    Color(0xFFFF7777),
    Color(0xFFFFFA77),
    Color(0xFF4462FF),
    Color(0xFF14FF00),
    Color(0xFFE231FF),
    Color(0xFF00FFFF),
    Color(0xFFF2A5FF)
)
@Composable
fun Home(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        TopHomeBar(
            onProfileIconClick = { onIntent(HomeIntent.NavigateToProfile) },
            profileImageUri = state.imageUri,
            username = state.username,
            onIntent = onIntent,
        )
        if (state.topAlbumsState is ContentLoadingState.Error ||
            state.topTracksState is ContentLoadingState.Error ||
            state.topArtistsState is ContentLoadingState.Error) {
            ErrorView(onIntent = { onIntent(HomeIntent.RetryFetchAll) })
        } else {
            MainContent(state = state,onIntent = onIntent)
        }
    }
}
@Composable
private fun MainContent(state: HomeState,onIntent: (HomeIntent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // --- Phần 1: Top Albums ---
        item {
            SectionHeader(
                title = stringResource(id = R.string.top_albums),
                onSeeAllClick = { onIntent(HomeIntent.NavigateToTopAlbums) }
            )
            when (state.topAlbumsState) {
                is ContentLoadingState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00C2CB))
                    }
                }
                is ContentLoadingState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .height(240.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        items(state.topAlbums.take(6)) { album ->
                            AlbumItem(album = album)
                        }
                    }
                }
                is ContentLoadingState.Error -> {}
            }
        }
        item {
            SectionHeader(title = stringResource(id = R.string.top_tracks), onSeeAllClick = { onIntent(HomeIntent.NavigateToTopTracks) })
            when (state.topTracksState) {
                is ContentLoadingState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00C2CB))
                    }
                }
                is ContentLoadingState.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(state.topTracks.take(5)) { index, track ->
                            val color = BarColors[index % BarColors.size]
                            TrackItem(track = track, color = color)
                        }
                    }
                }
                is ContentLoadingState.Error -> {}
            }
        }

        // --- Phần 3: Top Artists
        item {
            SectionHeader(title = stringResource(id = R.string.top_artists), onSeeAllClick = {  onIntent(HomeIntent.NavigateToTopArtists)})
            when (state.topArtistsState) {
                is ContentLoadingState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00C2CB))
                    }
                }
                is ContentLoadingState.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.topArtists.take(5)) { artist ->
                            ArtistItem(artist = artist)
                        }
                    }
                }
                is ContentLoadingState.Error -> {}
            }
        }
    }
}
@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(id = R.string.see_all),
            color = Color(color =0xFF00C2CB ),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onSeeAllClick),
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
fun AlbumItem(album: AlbumFromApi, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF282828))
            .clickable { /* Xử lý sau */ }
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(

            model = album.getExtraLargeImageUrl() ?: R.drawable.cofee,
            contentDescription = album.name,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.cofee)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = album.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = album.artist.name,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
private fun ErrorView(onIntent: () -> Unit) {
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
            text = stringResource(id = R.string.errormessage),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onIntent,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C2CB)),
            shape = RoundedCornerShape(16)
        ) {
            Text(text = stringResource(id = R.string.try_again), color = Color.White)
        }
    }
}
@Composable
fun TrackItem(
    track: TrackFromApi,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(160.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* Xử lý sau */ }
    ) {
        AsyncImage(
            model = track.getExtraLargeImageUrl() ?: R.drawable.meo2,
            contentDescription = track.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.meo2)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black), startY = 100f))
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = track.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(),
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = Offset(x = 4f, y = 4f),
                        blurRadius = 8f
                    )
                )
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.listenner), contentDescription = "Listens", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = track.listeners, color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.artist), contentDescription = "Artist", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = track.artist.name, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(4.dp)
                .background(color)
        )
    }
}

@Composable
fun ArtistItem(artist: ArtistFromApi, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { }
    ) {
        AsyncImage(
            model = artist.getExtraLargeImageUrl() ?: R.drawable.grainydays,
            contentDescription = artist.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.grainydays)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent), endY = 150f))
        )
        Text(
            text = artist.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.7f),
                    offset = Offset(x = 4f, y = 4f),
                    blurRadius = 8f
                )
            )
        )
    }
}
@Composable
fun TopHomeBar(
    modifier: Modifier = Modifier,
    onProfileIconClick: () -> Unit ,
    onIntent: (HomeIntent) -> Unit,
    profileImageUri: Uri? = null,
    username: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 15.dp, end = 15.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProfileImage(
                model = profileImageUri ?: R.drawable.meo2,
                size = 45.dp,
                modifier = Modifier.clickable(onClick = onProfileIconClick)
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
                .clickable  (onClick = onProfileIconClick)) {
                Text(text = stringResource(id = R.string.welcome_back), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = username, fontSize = 16.sp, color = Color.Gray)
            }
            Icon(
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .clickable(onClick = { onIntent(HomeIntent.NavigateToSettings) }),
                painter = painterResource(id = R.drawable.setting_icon),
                contentDescription = "setting icon", tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.rank), contentDescription = "Rankings", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.rankings), color = Color(color = 0xFF00C2CB), fontWeight = FontWeight.SemiBold, fontSize = 25.sp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    Home(
        state = HomeState(username = "Duc Trung", imageUri = null),
        onIntent = {}
    )
}
