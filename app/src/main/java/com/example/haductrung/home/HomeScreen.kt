package com.example.haductrung.home
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.haductrung.Home
import com.example.haductrung.Library
import com.example.haductrung.Playlist
import com.example.haductrung.R
@Composable
fun Home(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopHomeBar(onProfileIconClick = { onIntent(HomeIntent.NavigateToProfile) })
        Spacer(modifier = Modifier.weight(1f))
        Text("Home", fontSize = 50.sp)
        Spacer(modifier = Modifier.weight(1f))
    }
}
@Composable
fun TopHomeBar(modifier: Modifier = Modifier, onProfileIconClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.End
    ) {
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
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedColor = Color(0xFF00CED1)
    val unselectedColor = Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (currentRoute != Home::class.qualifiedName) {
                        navController.navigate(Home) {
                            popUpTo(Home) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isHomeSelected = currentRoute == Home::class.qualifiedName
            Icon(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home",
                tint = if (isHomeSelected) selectedColor else unselectedColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Home",
                color = if (isHomeSelected) selectedColor else unselectedColor,
                fontSize = 12.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (currentRoute != Library::class.qualifiedName) {
                        navController.navigate(Library) { launchSingleTop = true }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isLibrarySelected = currentRoute == Library::class.qualifiedName
            Icon(
                painter = painterResource(id = R.drawable.library),
                contentDescription = "Library",
                tint = if (isLibrarySelected) selectedColor else unselectedColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Library",
                color = if (isLibrarySelected) selectedColor else unselectedColor,
                fontSize = 12.sp
            )
        }


        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (currentRoute != Playlist::class.qualifiedName) {
                        navController.navigate(Playlist) { launchSingleTop = true }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isPlaylistSelected = currentRoute == Playlist::class.qualifiedName
            Icon(
                painter = painterResource(id = R.drawable.playlist),
                contentDescription = "Playlist",
                tint = if (isPlaylistSelected) selectedColor else unselectedColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Playlist",
                color = if (isPlaylistSelected) selectedColor else unselectedColor,
                fontSize = 12.sp
            )
        }
    }
}
@Preview
@Composable
fun Preview(){
    Home( state = HomeState(),{},Modifier)
}
@Preview
@Composable
fun BottomHomeBarPreview() {
    val navController = rememberNavController()
    BottomHomeBar(navController = navController)
}