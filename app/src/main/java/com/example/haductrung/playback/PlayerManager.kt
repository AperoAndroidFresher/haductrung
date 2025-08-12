package com.example.haductrung.playback

import android.app.Application
import com.example.haductrung.musicPlayerBar.PlayerViewModel

object PlayerManager {
    lateinit var viewModel: PlayerViewModel
    fun initialize(application: Application) {
        if (!this::viewModel.isInitialized) {
            viewModel = PlayerViewModel(application)
        }
    }

    fun isInitialized(): Boolean {
        return this::viewModel.isInitialized
    }
}