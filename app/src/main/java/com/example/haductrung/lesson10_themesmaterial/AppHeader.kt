package com.example.haductrung.lesson10_themesmaterial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
fun AppHeader(
    title: String,
    iconResId: Int,
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit,
    onIconLDClick: () -> Unit,
    isLight: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .padding(vertical = 20.dp).background(Color.Transparent)
    ) {
        val iconResIdLD = if (isLight) R.drawable.dark else R.drawable.light
        val contentDes = if (isLight) "light" else "dark"
        Image(
            painter = painterResource(iconResIdLD),
            contentDescription = contentDes,
            modifier = Modifier.size(27.dp).clickable {
                onIconLDClick()
            }
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        Image(
            painter = painterResource(id = iconResId),

            contentDescription = "Header Icon",

            modifier = Modifier
                .size(27.dp)
                .align(Alignment.CenterEnd)
                .clickable(onClick = onIconClick)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,

                    )
        )
    }
}