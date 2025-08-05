package com.example.haductrung.library.minicomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R


@Composable
fun CustomMenuItem(
    text: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .clickable (onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun CustomMenuItemPreview() {

    Column (modifier = Modifier.background(Color.DarkGray)) {
        CustomMenuItem(
            text = "Add to playlist",
            iconResId = R.drawable.addplaylist,
            onClick = {}
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
        CustomMenuItem(
            text = "Share",
            iconResId = R.drawable.share,
            onClick = {}
        )
    }
}