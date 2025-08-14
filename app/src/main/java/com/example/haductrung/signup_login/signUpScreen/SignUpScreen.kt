package com.example.haductrung.signup_login.signUpScreen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.haductrung.R
import com.example.haductrung.signup_login.minicomposale.FormatButton
import com.example.haductrung.signup_login.minicomposale.FormatTextField
import com.example.haductrung.signup_login.minicomposale.Header

@Composable
fun SignupScreen(
    state: SignUpState,
    onIntent: (SignUpIntent)->Unit

) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(stringResource(id = R.string.signup_action))
        Spacer(Modifier.height(40.dp))
        FormatTextField(
            placeholderText = stringResource(id = R.string.username_placeholder),
            value = state.username,
            onValueChange = {onIntent(SignUpIntent.OnUsernameChange(it))},
            iconResId = R.drawable.username,
            isError = state.usernameError != null,
            errorMessage = state.usernameError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = stringResource(id = R.string.password_placeholder),
            value = state.password,
            onValueChange = {onIntent(SignUpIntent.OnPasswordChange(it))},
            iconResId = R.drawable.password,
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick ={onIntent(SignUpIntent.OnTogglePasswordVisibility)} ) {
                    Image(
                        painter = painterResource(if (state.isPasswordVisible) R.drawable.eyeopen else R.drawable.eyeclose),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(25.dp)
                    )
                }
            },
            isError = state.passwordError != null,
            errorMessage = state.passwordError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = stringResource(id = R.string.confirm_password_placeholder),
            value = state.confirmPassword,
            onValueChange = {onIntent(SignUpIntent.OnConfirmPasswordChange(it))},
            iconResId = R.drawable.password,
            visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick ={onIntent(SignUpIntent.OnToggleConfirmPasswordVisibility)} ) {
                    Image(
                        painter = painterResource(if (state.isConfirmPasswordVisible) R.drawable.eyeopen else R.drawable.eyeclose),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(25.dp)
                    )
                }
            },
            isError = state.confirmPasswordError != null,
            errorMessage = state.confirmPasswordError
        )
        Spacer(Modifier.height(20.dp))
        FormatTextField(
            placeholderText = stringResource(id = R.string.email_placeholder),
            value = state.email,
            onValueChange = {onIntent(SignUpIntent.OnEmailChange(it))},
            iconResId = R.drawable.mail,
            isError = state.emailError != null,
            errorMessage = state.emailError
        )
        Spacer(Modifier.weight(1f))
        FormatButton(stringResource(id = R.string.signup_button), onClick =  {onIntent(SignUpIntent.OnSignUpClick)})
    }
}