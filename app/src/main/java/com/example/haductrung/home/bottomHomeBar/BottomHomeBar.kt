package com.example.haductrung.home.bottomHomeBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.haductrung.Home
import com.example.haductrung.Library
import com.example.haductrung.Playlist
import com.example.haductrung.R

@Composable
fun BottomHomeBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedColor = Color(0xFF00CED1)
    val unselectedColor = Color.White

    val items = listOf(
        BottomBarItem("Home", R.drawable.home, Home::class.qualifiedName!!, Home),
        BottomBarItem("Library", R.drawable.library, Library::class.qualifiedName!!, Library),
        BottomBarItem("Playlist", R.drawable.playlist, Playlist::class.qualifiedName!!, Playlist)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            BottomBarButton(
                label = item.label,
                iconId = item.iconId,
                isSelected = currentRoute == item.routeName,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            ) {
                if (currentRoute != item.routeName) {
                    navController.navigate(item.destination) {
                        if (item.label == "Home") {
                            popUpTo(Home) { inclusive = true }
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomBarButton(
    label: String,
    iconId: Int,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = label,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            color = if (isSelected) selectedColor else unselectedColor,
            fontSize = 12.sp
        )
    }
}


data class BottomBarItem(
    val label: String,
    val iconId: Int,
    val routeName: String,
    val destination: Any
)