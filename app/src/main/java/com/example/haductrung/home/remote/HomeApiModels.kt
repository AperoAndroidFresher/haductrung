package com.example.haductrung.home.remote

import com.google.gson.annotations.SerializedName

data class TopAlbumsResponse(
    @SerializedName("topalbums") val topAlbums: TopAlbumsContainer
)

data class TopAlbumsContainer(
    @SerializedName("album") val albumList: List<AlbumFromApi>
)
data class AlbumFromApi(
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artist: ArtistInfo,
    @SerializedName("image") val imageList: List<ImageInfo>
) {
    fun getExtraLargeImageUrl(): String? {
        return imageList.find { it.size == "extralarge" }?.url?.ifEmpty { null }
    }
}

data class ArtistInfo(
    @SerializedName("name") val name: String
)


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