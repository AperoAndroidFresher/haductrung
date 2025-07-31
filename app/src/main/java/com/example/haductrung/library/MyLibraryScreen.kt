package com.example.haductrung.library

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LibraryScreen(state: LibraryState) {
    val activity = (LocalActivity.current)
    BackHandler {
        activity?.finish()
    }


    Box(
        modifier = Modifier.fillMaxSize().background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}