package com.example.haductrung.home
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
@Composable
fun Home(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold (
        topBar = {
            TopHomeBar(
                modifier = modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                onProfileIconClick = { onIntent(HomeIntent.NavigateToProfile) }
            )
        },
        bottomBar = {
            BottomHomeBar(
                modifier = modifier
                    .fillMaxWidth(),
                onHomeButtonClick = { onIntent(HomeIntent.HomeTabClicked) },
                onLibraryButtonClick = { onIntent(HomeIntent.LibraryTabClicked) },
                onMyPlaylistButtonClick = { onIntent(HomeIntent.PlaylistTabClicked) }
            )
        }
    ){ innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Home", fontSize = 50.sp, modifier = Modifier.padding(top =300.dp))
        }
    }
}

@Composable
fun TopHomeBar(
    modifier: Modifier = Modifier,
    onProfileIconClick: ()->Unit = {}
){
    Row (
        modifier = modifier
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.End
    ){
        Icon(
            modifier = Modifier
                .size(30.dp, 30.dp)
                .padding(end = 10.dp)
                .clickable(onClick = onProfileIconClick),
            painter = painterResource(R.drawable.setting_icon),
            contentDescription = "setting icon"
        )
    }
}

@Composable
fun BottomHomeBar(
    modifier: Modifier = Modifier,
    onHomeButtonClick: ()->Unit = {},
    onLibraryButtonClick: ()->Unit = {},
    onMyPlaylistButtonClick: ()->Unit = {}
){
    Row(
        modifier = modifier
    ) {
        Button(
            modifier = modifier
                .weight(1f),
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onHomeButtonClick
        ) {
            Text("Home")

        }
        Button(
            modifier = modifier
                .weight(1f),
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onLibraryButtonClick
        ) {
            Text("Library")
        }
        Button(
            modifier = modifier
                .weight(1f),
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onMyPlaylistButtonClick
        ) {
            Text("My Playlist")
        }
    }
}

@Preview
@Composable
fun Preview(){
    Home( state = HomeState(),{},Modifier)
}