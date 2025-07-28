package com.example.haductrung.signup_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FormatTextField(
    placeholderText: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconResId: Int,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(8.dp),
            isError = isError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color(0xFF1E1E1E),
                errorContainerColor = Color(0xFF1E1E1E)
            ),
            placeholder = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = placeholderText,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = placeholderText,
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isError && errorMessage != null) errorMessage else " ",
            color = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
