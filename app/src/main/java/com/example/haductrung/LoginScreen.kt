package com.example.haductrung

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


@Composable
fun LoginScreen(
    username: String, onUsernameChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    isChecked: Boolean, onCheckedChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    SetNews: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Login to your account")
        Spacer(Modifier.height(40.dp))
        FormatTextField(
            "Username",
            username,
            onUsernameChange,
            R.drawable.username
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            "Password",
            password,
            onPasswordChange,
            R.drawable.password,
            PasswordVisualTransformation()
        )
        RememberMeCheckbox(
            isChecked,
            onCheckedChange
        )
        FormatButton(
            "Login",
            onLoginClick
        )
        Spacer(Modifier.weight(1f))
        TextSignup(
            "Don’t have an account?",
            "Sign up",
            SetNews
        )
    }
}