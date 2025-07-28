package com.example.haductrung.signup_login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextSignup(
    promptText: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(bottom = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(promptText, fontSize = 18.sp, color = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(
            actionText,
            color = Color(0xFF06A0B5),
            fontSize = 20.sp,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}