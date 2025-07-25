package com.example.haductrung

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormatButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color(0xFF06A0B5)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text, color = Color.White, fontSize = 20.sp)
    }
}