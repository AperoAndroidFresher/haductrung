package com.example.haductrung.profile
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.haductrung.R

@Composable
fun ProfileScreen(
    name: String, onNameChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    university: String,onUniversityChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    isEditing: Boolean,onEditClick: () -> Unit,
    onSubmitClick: () -> Unit,
    showPopup: Boolean, onDismissPopup: () -> Unit,
    nameError: String?,
    phoneError: String?,
    universityError: String?,
    onBack: () -> Unit

) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFdaf2f5))
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
                onIconClick = onEditClick

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
                    onValueChange = onNameChange,
                    placeholderText = "Enter your name...",
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    isError = nameError!=null,
                    errorMessage = nameError

                )
                LabeledInput(
                    label = "PHONE NUMBER",
                    value = phone,
                    onValueChange = onPhoneChange,
                    placeholderText = "Your phone number...",
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    isError = phoneError!=null,
                    errorMessage = phoneError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInput(
                label = "UNIVERSITY NAME",
                value = university,
                onValueChange = onUniversityChange,
                placeholderText = "Your University name...",
                enabled = isEditing,
                isError = universityError!=null,
                errorMessage = universityError

            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInput(
                label = "DESCRIBE YOURSELF",
                value = description,
                onValueChange = onDescriptionChange,
                placeholderText = "Enter a description...",
                height = 200.dp,
                enabled = isEditing
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isEditing) {
                PrimaryButton(
                    text = "SUBMIT",
                    onClick = onSubmitClick,
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 80.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = showPopup,
            enter = fadeIn()+ scaleIn(),
            exit = fadeOut()+ scaleOut()
        ) {
            SuccessPopup(onDismissRequest = onDismissPopup)
        }
        if (showPopup) {
            LaunchedEffect(Unit) {
                delay(2000L)
                onDismissPopup()
            }
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileScreen() {
//    HaductrungTheme {
//        ProfileScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewSuccessPopup() {
//    HaductrungTheme {
//        SuccessPopup(onDismissRequest = {})
//    }
//}
