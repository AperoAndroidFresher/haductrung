package com.example.haductrung.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.haductrung.R
import com.example.haductrung.profile.minicomposable.CircularProfileImage

// =====================================================================
// BƯỚC 1: CẬP NHẬT DATA CLASS VÀ DỮ LIỆU GIẢ
// =====================================================================

data class Album(val id: Int, val title: String, val artist: String, val coverResId: Int)
// Cập nhật Track để có thêm listenCount và progress
data class Track(val id: Int, val title: String, val artist: String, val coverResId: Int, val listenCount: String, val progress: Float)
data class Artist(val id: Int, val name: String, val avatarResId: Int)

val sampleAlbums = List(6) { index ->
    Album(index, "Album Name ${index + 1}", "Artist ${index + 1}", R.drawable.cofee)
}
// Cập nhật dữ liệu giả cho Track
val sampleTracks = listOf(
    Track(0, "Espresso", "Sabrina Carpenter", R.drawable.meo2, "972846", 0.6f),
    Track(1, "Chill Mix", "Sabrina Carpenter", R.drawable.grainydays, "972846", 0.8f),
    Track(2, "Modern", "Taylor", R.drawable.meo2, "1.2M", 0.3f),
    Track(3, "Palette", "Taylor", R.drawable.cofee, "850K", 0.9f),
    Track(4, "In Search of...", "Taylor", R.drawable.grainydays, "734K", 0.5f)
)
val sampleArtists = List(5) { index ->
    Artist(index, "TranDucBo", R.drawable.grainydays)
}

// =====================================================================
// BƯỚC 3: XÂY DỰNG MÀN HÌNH HOME HOÀN CHỈNH
// =====================================================================

@Composable
fun Home(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        TopHomeBar(
            onProfileIconClick = { onIntent(HomeIntent.NavigateToProfile) },
            profileImageUri = state.imageUri,
            username = state.username
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // --- Phần 1: Top Albums ---
            item {
                SectionHeader(title = "Top Albums", onSeeAllClick = { /* Xử lý sau */ })
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(240.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(sampleAlbums) { album ->
                        AlbumItem(album = album)
                    }
                }
            }

            // --- Phần 2: Top Tracks ---
            item {
                SectionHeader(title = "Top Tracks", onSeeAllClick = { /* Xử lý sau */ })
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sampleTracks) { track ->
                        TrackItem(track = track) // Dùng TrackItem đã được cập nhật
                    }
                }
            }

            // --- Phần 3: Top Artists ---
            item {
                SectionHeader(title = "Top Artists", onSeeAllClick = { /* Xử lý sau */ })
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sampleArtists) { artist ->
                        ArtistItem(artist = artist) // Dùng ArtistItem đã được cập nhật
                    }
                }
            }
        }
    }
}


// =====================================================================
// BƯỚC 2: CẬP NHẬT CÁC COMPOSABLE CON
// =====================================================================

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "See all",
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onSeeAllClick)
        )
    }
}

@Composable
fun AlbumItem(album: Album, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF282828))
            .clickable { /* Xử lý sau */ }
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = album.coverResId,
            contentDescription = album.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = album.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = album.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/**
 * SỬA ĐỔI HOÀN TOÀN COMPOSABLE NÀY
 */
@Composable
fun TrackItem(track: Track, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(160.dp)
            .aspectRatio(1f) // Giữ cho Box vuông
            .clip(RoundedCornerShape(16.dp))
            .clickable { /* Xử lý sau */ }
    ) {
        // Lớp ảnh nền
        AsyncImage(
            model = track.coverResId,
            contentDescription = track.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Lớp gradient đè lên ảnh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 100f // Bắt đầu gradient từ khoảng 1/3 ảnh
                    )
                )
        )

        // Lớp nội dung
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween // Đẩy nội dung chính xuống dưới
        ) {
            // Hàng trên cùng: Tên track
            Text(
                text = track.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Hàng dưới cùng: Lượt nghe và nghệ sĩ
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.username), contentDescription = "Listens", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = track.listenCount, color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.rank), contentDescription = "Artist", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = track.artist, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Lớp thanh progress
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.Gray.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(track.progress)
                    .height(4.dp)
                    .background(if (track.id % 2 == 0) Color(0xFFFE686A) else Color(0xFFF8D458)) // Đổi màu cho đa dạng
            )
        }
    }
}

/**
 * SỬA ĐỔI HOÀN TOÀN COMPOSABLE NÀY
 */
@Composable
fun ArtistItem(artist: Artist, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(140.dp) // Kích thước vuông
            .clip(RoundedCornerShape(16.dp))
            .clickable { /* Xử lý sau */ }
    ) {
        // Lớp ảnh nền
        AsyncImage(
            model = artist.avatarResId,
            contentDescription = artist.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Lớp gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                        endY = 150f // Gradient chỉ ở phần trên
                    )
                )
        )

        // Lớp tên nghệ sĩ
        Text(
            text = artist.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        )
    }
}


@Composable
fun TopHomeBar(
    modifier: Modifier = Modifier,
    onProfileIconClick: () -> Unit = {},
    profileImageUri: Uri? = null,
    username: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 15.dp, end = 15.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProfileImage(model = profileImageUri ?: R.drawable.meo2, size = 45.dp)
            Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                Text(text = "Welcome back !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = username, fontSize = 16.sp, color = Color.Gray)
            }
            Icon(modifier = Modifier.size(30.dp, 30.dp).clickable(onClick = onProfileIconClick), painter = painterResource(id = R.drawable.setting_icon), contentDescription = "setting icon", tint = Color.White)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.rank), contentDescription = "Rankings", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Rankings", color = Color(color = 0xFF00C2CB), fontWeight = FontWeight.SemiBold, fontSize = 25.sp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    Home(
        state = HomeState(username = "Duc Trung", imageUri = null),
        onIntent = {}
    )
}
