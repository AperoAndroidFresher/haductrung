package com.example.haductrung

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
                    AuthScreen()
                }
            }
        }
    }
}

enum class Screen {
    Welcome, Login, SignUp
}

@Composable
fun AuthScreen() {
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        Screen.Welcome -> WelcomeScreen(
            onTimeout = { currentScreen = Screen.Login }
        )
        Screen.Login -> LoginScreen(
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it },
            isChecked = rememberMe,
            onCheckedChange = { rememberMe = it },
            onLoginClick = {  },
            SetNews = {
                username = ""
                password = ""
                confirmPassword = ""
                email = ""
                usernameError = null
                passwordError = null
                confirmPasswordError = null
                emailError = null
                currentScreen = Screen.SignUp
            }
        )
        Screen.SignUp -> SignupScreen(
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = { confirmPassword = it },
            email = email,
            onEmailChange = { email = it },
            usernameError = usernameError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            emailError = emailError,
            onSignUpClick = {
                usernameError = null
                passwordError = null
                confirmPasswordError = null
                emailError = null
                var isValid = true

                if (username.isBlank() || !username.matches(Regex("^[a-zA-Z0-9]+$"))) {
                    usernameError = "invalid format"
                    isValid = false
                    username=""

                }

                if (password.isBlank() || !password.matches(Regex("^[a-zA-Z0-9]$"))) {
                    passwordError = "invalid format"
                    isValid = false
                    password=""

                }

                if (confirmPassword != password) {
                    confirmPasswordError = "invalid format"
                    isValid = false
                    confirmPassword=""
                }

                if (email.isBlank() || !email.matches(Regex("^[a-zA-Z0-9._-]+@apero\\.vn$"))) {
                    emailError = "invalid email"
                    isValid = false
                    email=""
                }

                if (isValid) {
                    currentScreen = Screen.Login
                }
            }
        )
    }
}


@Composable
fun Header(title: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 30.dp)) {
        Image(
            painterResource(R.drawable.logochaomung),
            contentDescription = "Header logo",
            modifier = Modifier.size(300.dp)
        )
        Text(
            title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 230.dp)
        )
    }
}

@Composable
fun TextSignup(
    promptText: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(bottom = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(promptText, fontSize = 18.sp, color = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(
            actionText,
            color = Color(0xFF06A0B5),
            fontSize = 20.sp,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewAuth() {
    HaductrungTheme {
        AuthScreen()
    }
}
