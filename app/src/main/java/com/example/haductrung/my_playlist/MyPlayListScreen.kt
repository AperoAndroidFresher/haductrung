package com.example.haductrung.my_playlist

import com.example.haductrung.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.haductrung.library.minicomposable.CustomMenuItem


@Composable
fun MyPlaylistScreen(
    state: PlaylistState,
    onIntent: (PlaylistIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlaylistTopBar(
                isGridView = state.isGridView,
                isSortMode = state.isSortMode,
                onIntent = onIntent
            )

            if (state.playlists.isEmpty()) {
                EmptyPlaylistView(onIntent = onIntent)
            } else {
                if (state.isGridView) {
                    PlaylistGridView(state = state, onIntent = onIntent)
                } else {
                    PlaylistListView(state = state, onIntent = onIntent)
                }
            }
        }

        // Gọi dialog tạo mơis
        if (state.showCreatePlaylistDialog) {
            PlaylistActionDialog(
                title = "New Playlist",
                confirmButtonText = "Create",
                onDismiss = { onIntent(PlaylistIntent.OnDismissCreatePlaylistDialog) },
                onConfirm = { name -> onIntent(PlaylistIntent.OnConfirmCreatePlaylist(name)) }
            )
        }

        // Gọi dialog đổi tên
        state.playlistToRename?.let { playlistToRename ->
            PlaylistActionDialog(
                title = "Rename Playlist",
                confirmButtonText = "Save",
                initialValue = playlistToRename.name,
                onDismiss = { onIntent(PlaylistIntent.OnDismissRenamePlaylistDialog) },
                onConfirm = { newName ->
                    onIntent(PlaylistIntent.OnConfirmRenamePlaylist(playlistToRename, newName))
                }
            )
        }
    }
}




@Composable
private fun PlaylistListView(state: PlaylistState, onIntent: (PlaylistIntent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = state.playlists,
            key = { "list-${it.id}" }
        ) { playlist ->
            PlaylistItem(
                playlist = playlist,
                isMenuExpanded = (state.playlistWithMenu == playlist.id),
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun PlaylistGridView(state: PlaylistState, onIntent: (PlaylistIntent) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = state.playlists,
            key = { "grid-${it.id}" }
        ) { playlist ->
            PlaylistGridItem(
                playlist = playlist,
                isMenuExpanded = (state.playlistWithMenu == playlist.id),
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    isMenuExpanded: Boolean,
    onIntent: (PlaylistIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onIntent(PlaylistIntent.OnPlaylistClick(playlist)) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.cofee),
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
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${playlist.songIds.size} songs",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        Box {
            Image(
                painter = painterResource(id = R.drawable.bacham),
                contentDescription = "More options",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onIntent(PlaylistIntent.OnMoreClick(playlist)) }
            )
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { onIntent(PlaylistIntent.OnDismissMenu) },
                modifier = Modifier.background(Color.DarkGray)
            ) {
                CustomMenuItem (
                    text = "Rename",
                    iconResId = R.drawable.rename,
                    onClick = {
                        onIntent(PlaylistIntent.OnRenamePlaylistClick(playlist))
                        onIntent(PlaylistIntent.OnDismissMenu)
                    }
                )
                CustomMenuItem(
                    text = "Remove Playlist",
                    iconResId = R.drawable.remove,
                    onClick = {
                        onIntent(PlaylistIntent.OnRemovePlaylist(playlist))
                        onIntent(PlaylistIntent.OnDismissMenu)
                    }
                )
            }
        }
    }
}
@Composable
private fun PlaylistGridItem(
    playlist: Playlist,
    isMenuExpanded: Boolean,
    onIntent: (PlaylistIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onIntent(PlaylistIntent.OnPlaylistClick(playlist)) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.cofee),
                contentDescription = playlist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.bacham),
                    contentDescription = "More options",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onIntent(PlaylistIntent.OnMoreClick(playlist)) }
                )
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { onIntent(PlaylistIntent.OnDismissMenu) },
                    modifier = Modifier.background(Color.DarkGray)
                ) {
                    CustomMenuItem(
                        text = "Rename",
                        iconResId = R.drawable.rename,
                        onClick = {
                            onIntent(PlaylistIntent.OnRenamePlaylistClick(playlist))
                            onIntent(PlaylistIntent.OnDismissMenu)
                        }
                    )
                    CustomMenuItem(
                        text = "Remove Playlist",
                        iconResId = R.drawable.remove,
                        onClick = {
                            onIntent(PlaylistIntent.OnRemovePlaylist(playlist))
                            onIntent(PlaylistIntent.OnDismissMenu)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${playlist.songIds.size} songs",
            color = Color.LightGray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun EmptyPlaylistView(onIntent: (PlaylistIntent) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "You don't have any playlists.\nClick the \"+\" button to add",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onIntent(PlaylistIntent.OnCreatePlaylistClick) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Create Playlist",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistTopBar(
    isGridView: Boolean,
    isSortMode: Boolean,
    onIntent: (PlaylistIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Playlists",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Create Playlist",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onIntent(PlaylistIntent.OnCreatePlaylistClick) })
            val viewIcon = if (isGridView) R.drawable.list else R.drawable.grid
            Image(
                painter = painterResource(id = viewIcon),
                contentDescription = "Toggle View",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onIntent(PlaylistIntent.OnToggleViewClick) })
            val sortIcon = if (isSortMode) R.drawable.tickv else R.drawable.sort
            Image(
                painter = painterResource(id = sortIcon),
                contentDescription = "Sort",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onIntent(PlaylistIntent.OnToggleSortClick) })
        }
    }
}


@Composable
private fun PlaylistActionDialog(
    title: String,
    confirmButtonText: String,
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    val customBlue = Color(0xFF38B6FF)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF282828))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    placeholder = { Text("Give your playlist a title", color = Color.Gray) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = customBlue,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = customBlue
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.Gray.copy(alpha = 0.3f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Divider(
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp),
                        color = Color.Gray.copy(alpha = 0.3f)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (text.isNotBlank()) onConfirm(text) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(confirmButtonText, color = customBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// CÁC PREVIEW

@Preview(name = "Create Dialog", showBackground = true)
@Composable
fun PlaylistActionDialog_CreatePreview() {
    Box(Modifier.background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        PlaylistActionDialog(title = "New Playlist", confirmButtonText = "Create", onDismiss = {}, onConfirm = {})
    }
}

@Preview(name = "Rename Dialog", showBackground = true)
@Composable
fun PlaylistActionDialog_RenamePreview() {
    Box(Modifier.background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        PlaylistActionDialog(title = "Rename Playlist", confirmButtonText = "Save", initialValue = "My Old Name", onDismiss = {}, onConfirm = {})
    }
}

@Preview(name = "Empty State", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MyPlaylistScreenEmptyPreview() {
    MyPlaylistScreen(
        state = PlaylistState(playlists = emptyList()),
        onIntent = {}
    )
}

@Preview(name = "List View", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MyPlaylistScreenListPreview() {
    MyPlaylistScreen(
        state = PlaylistState(playlists = listOf(
            Playlist(name = "Running Mix", songIds = listOf(1,2,3)),
            Playlist(name = "Sad Vibes", songIds = listOf(4,5))
        )),
        onIntent = {}
    )
}

@Preview(name = "Grid View", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MyPlaylistScreenGridPreview() {
    MyPlaylistScreen(
        state = PlaylistState(
            playlists = listOf(
                Playlist(name = "Running Mix", songIds = listOf(1,2,3)),
                Playlist(name = "Sad Vibes", songIds = listOf(4,5))
            ),
            isGridView = true
        ),
        onIntent = {}
    )
}