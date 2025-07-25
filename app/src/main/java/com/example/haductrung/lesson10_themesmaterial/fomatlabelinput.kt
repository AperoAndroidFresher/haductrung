package com.example.haductrung.lesson10_themesmaterial

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabeledInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null

) {
    val textFieldModifier = if (height != null) {
        Modifier
            .fillMaxWidth()
            .height(height)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = isError,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
            placeholder = {
                Text(
                    placeholderText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.surfaceDim

                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledContainerColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = textFieldModifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
