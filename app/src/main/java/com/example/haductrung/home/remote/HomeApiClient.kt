package com.example.haductrung.home.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Object này đóng vai trò là "Nhà máy", chuyên tạo ra một client
 */
object HomeApiClient {

    private const val BASE_URL = "https://ws.audioscrobbler.com/"
    private const val REQUEST_TIMEOUT = 30L

    private val gsonConfig = GsonBuilder().create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gsonConfig))
        .build()

    /**
     * Hàm này "sản xuất" ra một đối tượng TopAlbumApiService đã sẵn sàng để sử dụng.
     */
    fun build(): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }
}