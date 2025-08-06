package com.example.haductrung.library.remote


import retrofit2.http.GET

interface ApiService{
    @GET("techtrek/Remote_audio.json")
    suspend fun getRemoteSongs():  List<RemoteSong>
}