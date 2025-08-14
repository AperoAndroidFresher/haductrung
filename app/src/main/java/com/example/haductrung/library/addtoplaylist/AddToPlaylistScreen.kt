package com.example.haductrung.library.addtoplaylist
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.haductrung.R
import com.example.haductrung.database.Converters
import com.example.haductrung.database.entity.PlaylistEntity

@Composable
fun AddToPlaylistScreen(
    state: AddToPlaylistState,
    onIntent: (AddToPlaylistIntent) -> Unit,
    onBack: () -> Unit
) {
    Dialog(onDismissRequest = onBack) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF282828))
        ) {
            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.choose_playlist),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(20.dp)
                )

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.playlists.isEmpty() -> {
                        EmptyView(onIntent)
                    }
                    else -> {
                        PlaylistListView(
                            playlists = state.playlists,
                            onIntent = onIntent
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun EmptyView(onIntent: (AddToPlaylistIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
                .clickable { onIntent(AddToPlaylistIntent.NavigateToMyPlaylistTab) },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Navigate to create playlist",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun PlaylistListView(
    playlists: List<PlaylistEntity>,
    onIntent: (AddToPlaylistIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(playlists, key = { it.playlistId }) { playlist ->
            PlaylistSelectionItem(
                playlist = playlist,
                onClick = { onIntent(AddToPlaylistIntent.OnPlaylistSelected(playlist)) }
            )
        }
    }
}
@Composable
private fun PlaylistSelectionItem(
    playlist: PlaylistEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
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
        Column {
            Text(
                text = playlist.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            val songCount = Converters().fromString(playlist.songIdsJson).size
            Text(
                text = "$songCount ${stringResource(id = R.string.songs)}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
@Preview(name = "Empty State")
@Composable
private fun AddToPlaylistScreenEmptyPreview() {
    AddToPlaylistScreen(
        state = AddToPlaylistState(isLoading = false, playlists = emptyList()),
        onIntent = {},
        onBack = {}
    )
}