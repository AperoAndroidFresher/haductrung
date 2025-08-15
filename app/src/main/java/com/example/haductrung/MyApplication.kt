package com.example.haductrung

import android.app.Application
import com.example.haductrung.playback.PlayerManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerManager.initialize(this)
    }
}