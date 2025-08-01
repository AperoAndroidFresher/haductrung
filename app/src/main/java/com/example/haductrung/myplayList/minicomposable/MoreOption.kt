package com.example.haductrung.myplayList.minicomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.R
import com.example.haductrung.ui.theme.HaductrungTheme

@Composable
fun moreoption(onDeleteClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .clickable(onClick = onDeleteClick)
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.remove),
                contentDescription = "xoa",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Remove playlist",
                color = Color.White,
                fontSize = 15.sp
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.namechange),
                contentDescription = "Share",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Rename",
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun PreviewMoreOption() {
    HaductrungTheme {

            moreoption(onDeleteClick = {})

    }
}