package com.example.haductrung.library.minicomposable


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.haductrung.R
import com.example.haductrung.library.LibraryTab

@Composable
fun LibraryTabs(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TabButton(
            text = stringResource(id = R.string.local),
            isSelected = selectedTab == LibraryTab.LOCAL,
            onClick = { onTabSelected(LibraryTab.LOCAL) }
        )
        TabButton(
            text = stringResource(id = R.string.remote),
            isSelected = selectedTab == LibraryTab.REMOTE,
            onClick = { onTabSelected(LibraryTab.REMOTE) }
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF00C2CB) else Color.DarkGray
    val contentColor = if (isSelected) Color.Black else Color.White

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = Modifier.width(120.dp).padding(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryTabsPreview() {
    LibraryTabs(selectedTab = LibraryTab.REMOTE, onTabSelected = {})
}