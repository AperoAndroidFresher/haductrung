package com.example.haductrung.home.remote

import com.google.gson.annotations.SerializedName

/**
 * Lớp này đại diện cho toàn bộ phản hồi JSON từ API.
 * Nó chứa một đối tượng "topalbums".
 */
data class TopAlbumsResponse(
    @SerializedName("topalbums") val topAlbums: TopAlbumsContainer
)

/**
 * Lớp này chứa danh sách các album.
 * Tên trường "albumList" khớp với khóa "album" trong JSON.
 */
data class TopAlbumsContainer(
    @SerializedName("album") val albumList: List<AlbumFromApi>
)

/**
 * Đây là "khuôn mẫu" chính cho một Album.
 * Nó chỉ chứa những trường chúng ta cần: name, artist, và imageList.
 */
data class AlbumFromApi(
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artist: ArtistInfo,
    @SerializedName("image") val imageList: List<ImageInfo>
) {
    /**
     * Hàm tiện ích để lấy URL của ảnh có size "extralarge" một cách an toàn.
     * Nếu không tìm thấy hoặc URL rỗng, nó sẽ trả về null.
     */
    fun getExtraLargeImageUrl(): String? {
        return imageList.find { it.size == "extralarge" }?.url?.ifEmpty { null }
    }
}

/**
 * Khuôn mẫu cho đối tượng "artist" bên trong một album.
 * Chúng ta chỉ cần lấy "name".
 */
data class ArtistInfo(
    @SerializedName("name") val name: String
)

/**
 * Khuôn mẫu cho một đối tượng ảnh trong danh sách "image".
 * Chú ý: Khóa trong JSON là "#text", chúng ta dùng SerializedName để ánh xạ.
 */
data class ImageInfo(
    @SerializedName("#text") val url: String,
    @SerializedName("size") val size: String
)
/**
 * Lớp này đại diện cho toàn bộ phản hồi JSON từ API Top Tracks.
 */
data class TopTracksResponse(
    @SerializedName("toptracks") val topTracks: TopTracksContainer
)
/**
 * Lớp này chứa danh sách các bài hát (track).
 */
data class TopTracksContainer(
    @SerializedName("track") val trackList: List<TrackFromApi>
)

/**
 * Đây là "khuôn mẫu" chính cho một Track.
 * Nó chứa những trường chúng ta cần: name, listeners, artist, và imageList.
 */
data class TrackFromApi(
    @SerializedName("name") val name: String,
    @SerializedName("listeners") val listeners: String,
    @SerializedName("artist") val artist: ArtistInfo,
    @SerializedName("image") val imageList: List<ImageInfo>
) {
    /**
     * Hàm tiện ích để lấy URL của ảnh có size "extralarge".
     */
    fun getExtraLargeImageUrl(): String? {
        return imageList.find { it.size == "extralarge" }?.url?.ifEmpty { null }
    }
}
/**
 * Lớp này đại diện cho toàn bộ phản hồi JSON từ API Top Artists.
 */
data class TopArtistsResponse(
    @SerializedName("artists") val artists: TopArtistsContainer
)

/**
 * Lớp này chứa danh sách các nghệ sĩ.
 */
data class TopArtistsContainer(
    @SerializedName("artist") val artistList: List<ArtistFromApi>
)

/**
 * Đây là "khuôn mẫu" chính cho một Artist.
 * Nó chứa những trường chúng ta cần: name và imageList.
 */
data class ArtistFromApi(
    @SerializedName("name") val name: String,
    @SerializedName("image") val imageList: List<ImageInfo>
) {
    /**
     * Hàm tiện ích để lấy URL của ảnh có size "extralarge".
     */
    fun getExtraLargeImageUrl(): String? {
        // API này có thể có cả "mega", ưu tiên mega nếu có
        return imageList.find { it.size == "mega" }?.url?.ifEmpty { null }
            ?: imageList.find { it.size == "extralarge" }?.url?.ifEmpty { null }
    }
}