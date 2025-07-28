package com.example.haductrung.signup_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000L)
        onTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(R.drawable.logochaomung),
            "Welcome",
            Modifier.size(300.dp)
        )
        Text(
            "Apero Music",
            color = Color(0xFF427880),
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
    }
}