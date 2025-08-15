package com.example.haductrung.library.minicomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.haductrung.R
import com.example.haductrung.repository.Song


@Composable
fun SongGridItem(
    song: Song,
    isMenuExpanded: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onMoreClick: () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit
    ) {
    val backgroundColor = if (isSelected) Color.DarkGray else Color.Black
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.albumArtUri)
                    .crossfade(true)
                    .fallback(R.drawable.grainydays)
                    .error(R.drawable.cofee)
                    .build(),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Image(
                    painter = painterResource(id = R.drawable.bacham),
                    contentDescription = "moreoption",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .align(alignment = Alignment.TopEnd)
                        .clickable(onClick = onMoreClick),
                )
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onDismissMenu,
                    modifier = Modifier.background(color = Color.Black)
                ) {
                    menuContent()
                }

            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = song.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee()

        )
        Text(
            text = song.artist,
            color = Color.Gray,
            fontSize = 20.sp,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee()
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = song.duration,
            color = Color.LightGray,
            fontSize = 20.sp
        )
    }
}