package com.example.haductrung

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class Airplanemode : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {

            val intents = intent.getBooleanExtra("state", false)

            if (intents) {
                Log.d("AirplaneModeReceiver", "Chế độ máy bay đã BẬT")
                Toast.makeText(context, "Chế độ máy bay đã bật", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("AirplaneModeReceiver", "Chế độ máy bay đã Tắt")
                Toast.makeText(context, "Chế độ máy bay đã tắt", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
