package com.example.haductrung.musicPlayerBar

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.haductrung.R
import com.example.haductrung.repository.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    state: PlayerUiState,
    onIntent: (PlayerUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSong = state.currentPlayingSong ?: return
    var isSeeking by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf(0f) }

    @SuppressLint("DefaultLocale")
    fun formatDuration(ms: Long): String {
        if (ms < 0) return "00:00"
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),

        ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Debug: Số bài trong playlist = ${state.currentPlaylist?.size ?: 0}",
                color = Color.Red
            )
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onIntent(PlayerUiIntent.BackFromPlayerDetail) },
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                text = "Now Playing",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Image(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onIntent(PlayerUiIntent.DismissPlayer) }
                    .clip(RoundedCornerShape(8.dp)),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(currentSong.albumArtUri)
                .crossfade(true)
                .fallback(R.drawable.grainydays)
                .error(R.drawable.cofee)
                .build(),
            contentDescription = currentSong.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = currentSong.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = currentSong.artist,
            fontSize = 16.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
        Slider(
            value = if (isSeeking) sliderPosition else state.progress,
            onValueChange = {
                isSeeking = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                onIntent(PlayerUiIntent.Seek(sliderPosition))
                isSeeking = false
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTrackColor = Color(0xFF00C2CB),
                inactiveTrackColor = Color.Gray,
                thumbColor = Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFF00C2CB), CircleShape)
                        .border(0.dp, Color.Transparent, CircleShape)
                )
            }
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(
                    if (isSeeking) {
                        (sliderPosition * state.totalDuration).toLong()
                    } else {
                        state.currentPosition
                    }
                ),
                color = Color.LightGray
            )
            Text(text = formatDuration(state.totalDuration), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Bảng điều khiển ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(id = R.drawable.tron_2),
                contentDescription = "Shuffle",
                modifier = Modifier.size(28.dp).clickable {  },
                colorFilter = ColorFilter.tint(Color.White)
            )
            val isPreviousEnabled = state.isLoopingEnabled || state.currentSongIndex > 0
            // SỬA LẠI: Dùng Image
            Image(
                painter = painterResource(id = R.drawable.previous),
                contentDescription = "Previous",
                modifier = Modifier
                    .size(40.dp)
                    .clickable(enabled = isPreviousEnabled) {
                        onIntent(PlayerUiIntent.SkipToPrevious)
                    },
                // Làm mờ icon nếu bị vô hiệu hóa
                alpha = if (isPreviousEnabled) 1f else 0.5f,
                colorFilter = ColorFilter.tint(Color.White)
            )

            // SỬA LẠI: Dùng Box và Image cho nút Play/Pause để giữ nền tròn
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C2CB))
                    .clickable { onIntent(PlayerUiIntent.TogglePlayPause) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if (state.isPlaying) painterResource(id = R.drawable.pause) else painterResource(
                        id = R.drawable.play
                    ),
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            val isNextEnabled = state.isLoopingEnabled || state.currentSongIndex < (state.currentPlaylist?.size ?: 0) - 1
            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = "Next",
                modifier = Modifier
                    .size(40.dp)
                    .clickable(enabled = isNextEnabled) {
                        onIntent(PlayerUiIntent.SkipToNext)
                    },
                alpha = if (isNextEnabled) 1f else 0.5f,
                colorFilter = ColorFilter.tint(Color.White)
            )
            Image(
                painter = painterResource(id = R.drawable.tuantu),
                contentDescription = "Loop",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onIntent(PlayerUiIntent.ToggleLoopMode) },
                colorFilter = ColorFilter.tint(
                    if (state.isLoopingEnabled) Color(0xFF00C2CB) else Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
fun PlayerDetailScreenDynamicPreview() {
    val sampleSong = Song(
        id = 1,
        title = "Anh Không Làm Gì Đâu Anh...",
        artist = "Bennn",
        filePath = "",
        duration = "04:02",
        durationMs = 2000,
        albumArtUri = null
    )

    val previewState = PlayerUiState(
        currentPlayingSong = sampleSong,
        isPlaying = true,
        currentPosition = 75000L,
        totalDuration = 242000L,
        progress = 75000f / 242000f
    )

    PlayerDetailScreen(
        state = previewState,
        onIntent = {}
    )
}