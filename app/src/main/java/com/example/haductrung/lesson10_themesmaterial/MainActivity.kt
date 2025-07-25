package com.example.haductrung.lesson10_themesmaterial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.haductrung.R
import com.example.haductrung.ui.theme.AppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLightTheme by remember { mutableStateOf(true) }
            AppTheme(darkTheme = !isLightTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileScreen(
                        isLightTheme = isLightTheme,
                        onThemeToggle = { isLightTheme = !isLightTheme }
                    )
                }
            }
        }
    }
}
@Composable
fun ProfileScreen(
    isLightTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var universisyError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var popup by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp).clickable(
                    indication = null, //1
                    interactionSource = remember { MutableInteractionSource() },//2 (1+2 xóa gợn sóng)
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppHeader(
                title = "MY INFORMATION",
                iconResId = R.drawable.icon,
                onIconClick = { isEditing = true },
                onIconLDClick = onThemeToggle,
                isLight = isLightTheme

            )
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
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    isError = nameError != null,
                    errorMessage = nameError

                )
                LabeledInput(
                    label = "PHONE NUMBER",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholderText = "Your phone number...",
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    isError = phoneError != null,
                    errorMessage = phoneError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInput(
                label = "UNIVERSITY NAME",
                value = university,
                onValueChange = { university = it },
                placeholderText = "Your University name...",
                enabled = isEditing,
                isError = universisyError != null,
                errorMessage = universisyError

            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInput(
                label = "DESCRIBE YOURSELF",
                value = description,
                onValueChange = { description = it },
                placeholderText = "Enter a description...",
                height = 200.dp,
                enabled = isEditing
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isEditing) {
                PrimaryButton(
                    text = "SUBMIT",
                    onClick = {
                        nameError = null
                        phoneError = null
                        universisyError = null
                        var isValid = true
                        if (!name.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            isValid = false
                            nameError = "invalid format"
                        }
                        if (!phone.matches(Regex("^0\\d{9}$"))) {
                            isValid = false
                            phoneError = "invalid format"
                        }
                        if (!university.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            isValid = false
                            universisyError = "invalid format"
                        }
                        if (isValid) {
                            isEditing = false
                            popup = true
                        }
                    },
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 80.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = popup,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            SuccessPopup(onDismissRequest = { popup = false })
        }
        if (popup) {
            LaunchedEffect(Unit) {
                delay(2000L)
                popup = false
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    AppTheme {
        ProfileScreen(true, onThemeToggle = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSuccessPopup() {
    AppTheme {
        SuccessPopup(onDismissRequest = {})
    }
}