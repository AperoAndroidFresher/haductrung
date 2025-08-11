package com.example.haductrung.musicPlayerBar

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.repository.Song


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPlayerScreen(
    state: PlayerUiState,
    onIntent: (PlayerUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSong = state.currentPlayingSong ?: return

    @SuppressLint("DefaultLocale")
    fun formatDuration(ms: Long): String {
        if (ms < 0) return "00:00"
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // YÊU CẦU 1: ROW CHỨA DẤU X
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(
                    color = Color.Gray,
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(state.progress)
                    .height(5.dp)
                    .background(
                        color = Color(0xFF00C2CB),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .clickable { onIntent(PlayerUiIntent.OpenPlayerDetail) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onIntent(PlayerUiIntent.TogglePlayPause) }) {
                        Icon(
                            painter = painterResource(id = if (state.isPlaying) R.drawable.pause else R.drawable.play),
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentSong.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                }
                Text(
                    text = formatDuration(state.currentPosition),
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    modifier= Modifier.padding(end =8.dp)
                )
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun BottomPlayerScreenPreview() {
    // 1. Tạo dữ liệu giả để hiển thị
    val sampleSong = Song(
        id = 1,
        title = "Anh Không Làm Gì Đâu Anh Thề dsdkjsdf",
        artist = "Bennn",
        filePath = "", // không cần cho preview
        duration = "04:02",
        durationMs = 2000,
        albumArtUri = null,

    )

    val previewState = PlayerUiState(
        currentPlayingSong = sampleSong,
        isPlaying = true, // Hiển thị icon Pause
        currentPosition = 75000L, // 1 phút 15 giây
        totalDuration = 242000L,  // 4 phút 02 giây
        progress = 75000f / 242000f // Thanh tiến trình chạy được khoảng 30%
    )

    // 2. Gọi Composable chính của bạn với dữ liệu giả
    BottomPlayerScreen(
        state = previewState,
        onIntent = {} // Đối với preview, không cần xử lý sự kiện
    )
}
