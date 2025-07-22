package com.example.haductrung

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haductrung.ui.theme.HaductrungTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HaductrungTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun AppHeader(
    title: String,
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = title,
            color = Color.Black,
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
        )
    }
}

@Composable
fun CircularProfileImage(imageResId: Int, size: Dp = 120.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Profile Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    )
}

@Composable
fun LabeledInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    height: Dp? = null
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
            color = Color.Gray,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholderText, fontSize = 12.sp, color = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFedf4f5),
                unfocusedContainerColor = Color(0xFFedf4f5),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = textFieldModifier
                .border(
                    width = 1.dp,
                    color = Color(0xFF576061),
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(text, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFdaf2f5))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader(title = "MY INFORMATION", iconResId = R.drawable.icon)
        Spacer(modifier = Modifier.height(10.dp))
        CircularProfileImage(imageResId = R.drawable.meo2)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledInput(
                label = "NAME",
                value = name,
                onValueChange = { name = it },
                placeholderText = "Enter your name...",
                modifier = Modifier.weight(1f)
            )
            LabeledInput(
                label = "PHONE NUMBER",
                value = phone,
                onValueChange = { phone = it },
                placeholderText = "Your phone number...",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LabeledInput(
            label = "UNIVERSITY NAME",
            value = university,
            onValueChange = { university = it },
            placeholderText = "Your University name..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabeledInput(
            label = "DESCRIBE YOURSELF",
            value = description,
            onValueChange = { description = it },
            placeholderText = "Enter a description...",
            height = 200.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "SUBMIT",
            onClick = { },
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 80.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewProfileScreen() {
    HaductrungTheme {
        ProfileScreen()
    }
}