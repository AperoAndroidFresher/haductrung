package com.example.haductrung.signup_login.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.haductrung.R
import com.example.haductrung.signup_login.minicomposale.FormatButton
import com.example.haductrung.signup_login.minicomposale.FormatTextField
import com.example.haductrung.signup_login.minicomposale.Header
import com.example.haductrung.signup_login.minicomposale.RememberMeCheckbox
import com.example.haductrung.signup_login.minicomposale.TextSignup

@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Login to your account")
        Spacer(Modifier.height(40.dp))
        FormatTextField(
            placeholderText = "Username",
            value = state.username,
            onValueChange = { onIntent(LoginIntent.OnUsernameChange(it)) },
            iconResId = R.drawable.username
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = "Password",
            value = state.password,
            onValueChange = { onIntent(LoginIntent.OnPasswordChange(it)) },
            iconResId = R.drawable.password,
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onIntent(LoginIntent.OnTogglePasswordVisibility) }) {
                    Image(
                        painter = painterResource(if (state.isPasswordVisible) R.drawable.eyeopen else R.drawable.eyeclose),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        )
        RememberMeCheckbox(
            isChecked = state.isChecked,
            onCheckedChange = { onIntent(LoginIntent.OnCheckedChange(it)) }
        )
        FormatButton(
            text = "Login",
            onClick = { onIntent(LoginIntent.OnLoginClick) }
        )
        Spacer(Modifier.weight(1f))
        TextSignup(
            promptText = "Don’t have an account?",
            actionText = "Sign up",
            onActionClick = { onIntent(LoginIntent.SetNews) } 
        )
    }
}