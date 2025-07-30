package com.example.haductrung

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.haductrung.home.Home
import com.example.haductrung.home.HomeEvent
import com.example.haductrung.home.HomeViewModel
import com.example.haductrung.library.LibraryScreen
import com.example.haductrung.library.LibraryViewModel
import com.example.haductrung.profile.ProfileEvent
import com.example.haductrung.profile.ProfileIntent
import com.example.haductrung.profile.ProfileScreen
import com.example.haductrung.profile.ProfileViewModel
import com.example.haductrung.signup_login.LoginScreen.LoginEvent
import com.example.haductrung.signup_login.LoginScreen.LoginScreen
import com.example.haductrung.signup_login.LoginScreen.LoginViewModel
import com.example.haductrung.signup_login.SignUpScreen.SignUpEvent
import com.example.haductrung.signup_login.SignUpScreen.SignUpViewModel
import com.example.haductrung.signup_login.SignUpScreen.SignupScreen
import com.example.haductrung.signup_login.minicomposale.WelcomeScreen
import com.example.haductrung.song.SongScreen
import com.example.haductrung.song.SongViewModel
import com.example.haductrung.ui.theme.HaductrungTheme
import kotlinx.serialization.Serializable

@Serializable
object Welcome

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object Home

@Serializable
object Profile

@Serializable
object Library

@Serializable
object Playlist

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HaductrungTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val viewModel: ProfileViewModel = viewModel()
    val viewModelsong: SongViewModel = viewModel()
    val viewModelSU: SignUpViewModel = viewModel()
    val viewModellg: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Welcome
    ) {
        composable<Welcome> {
            WelcomeScreen(
                onTimeout = {
                    navController.navigate(Login) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                }
            )
        }

        composable<Login> {

            val state by viewModellg.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModellg.event.collect { event ->
                    when (event) {
                        is LoginEvent.NavigateToHome -> {
                            navController.navigate(Home) {
                                popUpTo(Login) { inclusive = true }
                            }
                        }
                        is LoginEvent.NavigateToSignUp -> {
                            navController.navigate(SignUp)
                        }
                    }
                }
            }
            LoginScreen(
                state = state,
                onIntent = viewModellg::processIntent
            )
        }
        composable<SignUp> {

            val state by viewModelSU.state.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                viewModelSU.event.collect { event ->
                    when (event) {
                        is SignUpEvent.NavigateBackToLogin -> {

                            navController.previousBackStackEntry
                                ?.savedStateHandle?.set("username", event.username)
                            navController.previousBackStackEntry
                                ?.savedStateHandle?.set("password", event.password)

                            navController.popBackStack()
                        }

                    }
                }
            }
            SignupScreen(
                state = state,
                onIntent = viewModelSU::processIntent
            )
        }

        composable<Home> {
            val viewModelh: HomeViewModel = viewModel()

            val state by viewModelh.state.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                viewModelh.event.collect { event ->
                    when (event) {
                        is HomeEvent.NavigateToProfile -> navController.navigate(Profile)
                        is HomeEvent.NavigateToLibrary -> navController.navigate(Library)
                        is HomeEvent.NavigateToPlaylist -> navController.navigate(Playlist)
                    }
                }
            }

            Home(
                state = state,
                onIntent = viewModelh::processIntent
            )
        }

        composable<Playlist> {

            val state by viewModelsong.state.collectAsStateWithLifecycle()
            SongScreen(
                state = state,
                onIntent = viewModelsong::processIntent
            )
        }

        composable<Library> {
            val viewModell: LibraryViewModel = viewModel()
            val state by viewModell.state.collectAsStateWithLifecycle()
            LibraryScreen(state = state)

        }

        composable<Profile> {

            val state by viewModel.state.collectAsStateWithLifecycle()

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                viewModel.processIntent(ProfileIntent.OnAvatarChange(uri))
            }

            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when (event) {
                        is ProfileEvent.OpenImagePicker -> {
                            imagePickerLauncher.launch("image/*")
                        }

                        is ProfileEvent.NavigateBack -> {
                            navController.popBackStack()
                        }

                        is ProfileEvent.ShowSuccessPopup -> {

                        }
                    }
                }
            }
            ProfileScreen(
                state = state,
                onIntent = viewModel::processIntent,
                eventFlow = viewModel.event
            )
        }
    }
}