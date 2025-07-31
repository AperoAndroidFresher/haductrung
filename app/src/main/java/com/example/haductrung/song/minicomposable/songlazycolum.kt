package com.example.haductrung.song.minicomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import com.example.haductrung.R


@Composable
fun SongItem(
    song: Song,
    modifier: Modifier = Modifier,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoreClick: () -> Unit,
    isSortMode: Boolean

) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.albumArtUri,
            imageLoader = imageLoader,
            contentDescription = song.title,
            placeholder = painterResource(id = R.drawable.grainydays),
            error = painterResource(id = R.drawable.cofee),
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                song.artist,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                song.duration,
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Box {
                val iconResId3 = if (isSortMode) R.drawable.hbg else R.drawable.bacham
                val contenDes3 = if (isSortMode) "sortting" else "normal"
                Image(
                    painter = painterResource(iconResId3),
                    contentDescription = contenDes3,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (!isSortMode) {
                                onMoreClick()
                            }
                        }
                )
                if(!isSortMode) {
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = onDismissMenu,
                        modifier = Modifier
                            .background(color = Color.Black)
                            .clip(RoundedCornerShape(16.dp)),
                    ) {
                        DeleteSong(onDeleteClick = onDeleteClick)
                    }
                }
            }
        }
    }
}
