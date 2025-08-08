package com.example.haductrung.signup_login.minicomposale

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R

@Composable
fun Header(title: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 30.dp)) {
        Image(
            painterResource(R.drawable.logochaomung),
            contentDescription = "Header logo",
            modifier = Modifier.size(300.dp)
        )
        Text(
            title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 230.dp)
        )
    }
}