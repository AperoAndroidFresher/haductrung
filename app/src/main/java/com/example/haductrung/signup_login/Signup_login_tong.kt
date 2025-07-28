package com.example.haductrung.signup_login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.haductrung.ui.theme.HaductrungTheme

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

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

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
            onLoginClick = { },
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
                isPasswordVisible = false
            },
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
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
                    username = ""
                }

                if (password.isBlank() || !password.matches(Regex("^[a-zA-Z0-9]+$"))) {
                    passwordError = "invalid format"
                    isValid = false
                    password = ""
                }

                if (confirmPassword != password) {
                    confirmPasswordError = "invalid format"
                    isValid = false
                    confirmPassword = ""
                }

                if (email.isBlank() || !email.matches(Regex("^[a-zA-Z0-9._-]+@apero\\.vn$"))) {
                    emailError = "invalid email"
                    isValid = false
                    email = ""
                }

                if (isValid) {
                    currentScreen = Screen.Login
                }
            },

            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            onToggleConfirmPasswordVisibility = {
                isConfirmPasswordVisible = !isConfirmPasswordVisible
            }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewAuth() {
    HaductrungTheme {

//        LoginScreen(
//            "fdf",
//            onUsernameChange = { },
//            "password",
//            onPasswordChange = { },
//            isChecked = true,
//            onCheckedChange = { },
//            onLoginClick = { },
//            SetNews = {
//            },
//            isPasswordVisible = true,
//            onTogglePasswordVisibility = { })
//    }
    }
}