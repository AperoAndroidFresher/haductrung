package com.example.haductrung.musicPlayerManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.repository.Song
import com.example.haductrung.ui.theme.HaductrungTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPlayerScreen(
    state: PlayerUiState,
    onIntent: (PlayerUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSong = state.currentPlayingSong ?: return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { onIntent(PlayerUiIntent.DismissPlayer) }) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { onIntent(PlayerUiIntent.TogglePlayPause) }) {
                Icon(
                    painter = painterResource(
                        id = if (state.isPlaying) R.drawable.pause else R.drawable.play
                    ),
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))


            Text(
                text = currentSong.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = currentSong.duration,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}


// --- Preview ---
@Preview
@Composable
private fun BottomPlayerScreenPreview() {
    val sampleSong = Song(
        id = 1,
        title = "Anh Không Làm Gì Đâu Anh Thề Đây Là Một Cái Tên Rất Dài",
        artist = "moody.",
        duration = "04:30",
        filePath = "",
        durationMs = 270000,
        albumArtUri = null
    )
    HaductrungTheme {
        BottomPlayerScreen(
            state = PlayerUiState(
                currentPlayingSong = sampleSong,
                isPlaying = true
            ),
            onIntent = {}
        )
    }
}