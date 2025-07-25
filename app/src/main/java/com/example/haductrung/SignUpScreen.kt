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
fun SignupScreen(
    username: String, onUsernameChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmPasswordChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    usernameError: String?, passwordError: String?,
    confirmPasswordError: String?, emailError: String?
) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Sign Up")
        Spacer(Modifier.height(40.dp))
        FormatTextField(
            "Username",
            username,
            onUsernameChange,
            R.drawable.username,
            isError = usernameError !=null,
            errorMessage = usernameError

        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            "Password",
            password,
            onPasswordChange,
            R.drawable.password,
            PasswordVisualTransformation(),
            passwordError != null,
            passwordError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            "Confirm password",
            confirmPassword,
            onConfirmPasswordChange,
            R.drawable.password,
            PasswordVisualTransformation(),
            confirmPasswordError != null,
            confirmPasswordError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            "Email",
            email,
            onEmailChange,
            R.drawable.mail,
            isError = emailError != null,
            errorMessage = emailError
        )
        Spacer(Modifier.weight(1f))
        FormatButton("Sign Up", onSignUpClick)
    }
}