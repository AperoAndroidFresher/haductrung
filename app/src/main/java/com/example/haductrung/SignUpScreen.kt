package com.example.haductrung

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignupScreen(
    username: String, onUsernameChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmPasswordChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    usernameError: String?, passwordError: String?,
    confirmPasswordError: String?, emailError: String?,
    // Thêm các tham số mới
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isConfirmPasswordVisible: Boolean,
    onToggleConfirmPasswordVisibility: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Sign Up")
        Spacer(Modifier.height(40.dp))
        FormatTextField(
            placeholderText = "Username",
            value = username,
            onValueChange = onUsernameChange,
            iconResId = R.drawable.username,
            isError = usernameError != null,
            errorMessage = usernameError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = "Password",
            value = password,
            onValueChange = onPasswordChange,
            iconResId = R.drawable.password,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Image(
                        painter = painterResource(if (isPasswordVisible) R.drawable.eyeopen else R.drawable.eyeclose),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(25.dp)
                    )
                }
            },
            isError = passwordError != null,
            errorMessage = passwordError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = "Confirm password",
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            iconResId = R.drawable.password,
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleConfirmPasswordVisibility) {
                    Image(
                        painter = painterResource(if (isConfirmPasswordVisible) R.drawable.eyeopen else R.drawable.eyeclose),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(25.dp)
                    )
                }
            },
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = "Email",
            value = email,
            onValueChange = onEmailChange,
            iconResId = R.drawable.mail,
            isError = emailError != null,
            errorMessage = emailError
        )
        Spacer(Modifier.weight(1f))
        FormatButton("Sign Up", onSignUpClick)
    }
}