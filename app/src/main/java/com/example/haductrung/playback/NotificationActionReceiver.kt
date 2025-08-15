package com.example.haductrung.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.haductrung.musicPlayerBar.PlayerUiIntent

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        // Kiểm tra xem ViewModel đã được khởi tạo chưa
        if (PlayerManager.isInitialized()) {
            when (action) {
                "ACTION_PREVIOUS" -> {
                    PlayerManager.viewModel.processIntent(PlayerUiIntent.SkipToPrevious)
                }
                "ACTION_PLAY_PAUSE" -> {
                    PlayerManager.viewModel.processIntent(PlayerUiIntent.TogglePlayPause)
                }
                "ACTION_NEXT" -> {
                    PlayerManager.viewModel.processIntent(PlayerUiIntent.SkipToNext)
                }
                "ACTION_CLOSE" -> {
                    PlayerManager.viewModel.processIntent(PlayerUiIntent.DismissPlayer)
                }
            }
        } else {
            // Trường hợp ứng dụng bị kill và Receiver nhận Intent,
            // chúng ta sẽ gửi Intent trực tiếp đến Service để xử lý.
            val serviceIntent = Intent(context, MusicPlaybackService::class.java).apply {
                this.action = action
            }
            context?.startService(serviceIntent)
        }
    }
}