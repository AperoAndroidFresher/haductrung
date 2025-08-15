package com.example.haductrung.library.minicomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.haductrung.R
import com.example.haductrung.repository.Song


@Composable
fun SongItem(
    song: Song,
    isMenuExpanded: Boolean,
    isSortMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onMoreClick: () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit

) {
    val backgroundColor = if (isSelected) Color.DarkGray else Color.Black
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArtUri)
                .crossfade(true)
                .fallback(R.drawable.grainydays)
                .error(R.drawable.cofee)
                .build(),
            contentDescription = song.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
            Text(
                song.artist,
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
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
                val contentDes3 = if (isSortMode) "sort" else "normal"
                Image(
                    painter = painterResource(iconResId3),
                    contentDescription = contentDes3,
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
                        modifier = Modifier.background(color = Color.Black)
                    ) {
                        menuContent()
                    }
                }
            }
        }
    }
}
