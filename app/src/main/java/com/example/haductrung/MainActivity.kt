package com.example.haductrung

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.haductrung.ui.theme.HaductrungTheme
import kotlinx.coroutines.delay

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
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp).background(Color.Transparent)
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
                .align(Alignment.CenterEnd).clickable(onClick = onIconClick)
        )
    }
}

@Composable
fun CircularProfileImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
    size: Dp = 120.dp
) {
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
    height: Dp? = null,
    enabled: Boolean = true,
    isError : Boolean= false,
    errorMessage:String?=null

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
            enabled = enabled,
            isError = isError,
            textStyle = TextStyle(color = Color.Black),
            placeholder = { Text(placeholderText, fontSize = 12.sp, color = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFedf4f5),
                unfocusedContainerColor = Color(0xFFedf4f5),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledContainerColor = Color(0xFFedf4f5)
            ),
            modifier = textFieldModifier
                .border(
                    width = 1.dp,
                    color = Color(0xFF576061),
                    shape = RoundedCornerShape(16.dp)
                )
        )
        if(isError && errorMessage!=null){
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(
            text,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun SuccessPopup(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.padding(32.dp).size(width = 353.dp, height = 328.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.correct),
                    contentDescription = "correct",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SUCCESS !",
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = Color.Green
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your information has\nbeen updated!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}



@Composable
fun ProfileScreen() {
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
                onIconClick = { isEditing = true }

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
                    isError = nameError!=null,
                    errorMessage = nameError

                )
                LabeledInput(
                    label = "PHONE NUMBER",
                    value = phone,
                    onValueChange = { phone = it },
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
                onValueChange = { university = it },
                placeholderText = "Your University name...",
                enabled = isEditing,
                isError = universisyError!=null,
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
                            nameError= "invalid format"
                        }
                        if (!phone.matches(Regex("^0\\d{9}$"))) {
                            isValid = false
                            phoneError= "invalid format"
                        }
                        if (!university.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            isValid = false
                            universisyError= "invalid format"
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
            enter = fadeIn()+ scaleIn(),
            exit = fadeOut()+ scaleOut()
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
    HaductrungTheme {
        ProfileScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSuccessPopup() {
    HaductrungTheme {
        SuccessPopup(onDismissRequest = {})
    }
}