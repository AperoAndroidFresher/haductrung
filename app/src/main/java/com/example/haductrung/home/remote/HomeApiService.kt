package com.example.haductrung.home.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface này định nghĩa các endpoint của API để lấy Top Albums.
 * Nó tương đương với file ApiService.kt cũ của bạn.
 */
interface HomeApiService {

    /**
     * Hàm này lấy danh sách Top Albums của một nghệ sĩ.
     * @GET("2.0/") chỉ định đường dẫn cố định của API.
     * @Query(...) dùng để thêm các tham số vào URL.
     */
    @GET("2.0/")
    suspend fun getTopAlbums(
        @Query("method") method: String = "artist.gettopalbums",
        @Query("mbid") mbid: String = "f9b593e6-4503-414c-99a0-46595ecd2e23", // mbid của Taylor Swift
        @Query("api_key") apiKey: String = "e65449d181214f936368984d4f4d4ae8",
        @Query("format") format: String = "json"
    ): TopAlbumsResponse
    @GET("2.0/")
    suspend fun getTopTracks(
        @Query("method") method: String = "artist.gettoptracks",
        @Query("mbid") mbid: String = "f9b593e6-4503-414c-99a0-46595ecd2e23",
        @Query("api_key") apiKey: String = "e65449d181214f936368984d4f4d4ae8",
        @Query("format") format: String = "json"
    ): TopTracksResponse
    @GET("2.0/")
    suspend fun getTopArtists(
        @Query("method") method: String = "chart.gettopartists",
        @Query("api_key") apiKey: String = "e65449d181214f936368984d4f4d4ae8",
        @Query("format") format: String = "json"
    ): TopArtistsResponse
}