package com.example.haductrung.profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.haductrung.R
import com.example.haductrung.profile.minicomposable.AppHeader
import com.example.haductrung.profile.minicomposable.CircularProfileImage
import com.example.haductrung.profile.minicomposable.LabeledInput
import com.example.haductrung.profile.minicomposable.PrimaryButton
import com.example.haductrung.profile.minicomposable.SuccessPopup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    eventFlow: Flow<ProfileEvent>,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var showPopup by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        eventFlow.collectLatest { event ->
            when (event) {
                is ProfileEvent.ShowSuccessPopup -> {
                    showPopup = true
                    delay(2000L)
                    showPopup = false
                }
                else -> {}
            }
        }
    }
    BackHandler {
        onIntent(ProfileIntent.OnBack)
    }

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
                onIconClick = { onIntent(ProfileIntent.onEditClick) }
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProfileImage(
                    model = state.imageUri ?: R.drawable.meo2,
                    size = 120.dp,
                    modifier = Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                )
                if (state.isEditing) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Change Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (30).dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .alpha(0.6f)
                            .clickable { onIntent(ProfileIntent.onAvatarClick) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledInput(
                    label = "NAME",
                    value = state.name,
                    onValueChange = {onIntent(ProfileIntent.onNameChange(it))},
                    placeholderText = "Enter your name...",
                    modifier = Modifier.weight(1f),
                    enabled = state.isEditing,
                    isError = state.nameError != null,
                    errorMessage = state.nameError

                )
                LabeledInput(
                    label = "PHONE NUMBER",
                    value = state.phone,
                    onValueChange ={onIntent(ProfileIntent.onPhoneChange(it))} ,
                    placeholderText = "Your phone number...",
                    modifier = Modifier.weight(1f),
                    enabled = state.isEditing,
                    isError = state.phoneError != null,
                    errorMessage = state.phoneError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInput(
                label = "UNIVERSITY NAME",
                value = state.university,
                onValueChange = {onIntent(ProfileIntent.onUniversityChange(it))},
                placeholderText = "Your University name...",
                enabled = state.isEditing,
                isError = state.universityError != null,
                errorMessage = state.universityError

            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInput(
                label = "DESCRIBE YOURSELF",
                value = state.description,
                onValueChange = {onIntent(ProfileIntent.onDescriptionChange(it))},
                placeholderText = "Enter a description...",
                height = 200.dp,
                enabled = state.isEditing
            )
            Spacer(modifier = Modifier.weight(1f))
            if (state.isEditing) {
                PrimaryButton(
                    text = "SUBMIT",
                    onClick = {onIntent(ProfileIntent.onSubmitClick)},
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 80.dp)
                )
            }
        }
        AnimatedVisibility(visible = showPopup) {
            SuccessPopup(onDismissRequest = { showPopup = false })
        }
    }
}


