package com.example.haductrung.myplayList.minicomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.myplayList.Playlist // Import data class Playlist của bạn
import com.example.haductrung.R
import com.example.haductrung.ui.theme.HaductrungTheme

@Composable
fun PlaylistItem(
    playlist: Playlist,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlaylistClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.grainydays),
            contentDescription = playlist.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.width(16.dp))


        Column(Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "${playlist.songs.size} songs",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }


        Box {
            Image(
                painter = painterResource(R.drawable.bacham),
                contentDescription = "More options",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onMoreClick)
            )


            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu,
                modifier = Modifier.background(Color(0xFF2E2E2E))
            ) {

                moreoption(onDeleteClick = onDeleteClick)
            }
        }
    }
}



