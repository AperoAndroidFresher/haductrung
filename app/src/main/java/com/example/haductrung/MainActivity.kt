package com.example.haductrung

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.haductrung.home.Home
import com.example.haductrung.home.HomeEvent
import com.example.haductrung.home.HomeViewModel
import com.example.haductrung.library.LibraryEvent
import com.example.haductrung.library.LibraryIntent
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
import com.example.haductrung.library.LibraryRepository
import com.example.haductrung.library.LibraryScreen
import com.example.haductrung.library.LibraryViewModel
import com.example.haductrung.library.minicomposable.AddToPlaylistDialog
import com.example.haductrung.library.minicomposable.Song
import com.example.haductrung.myplayList.MyPlaylistEvent
import com.example.haductrung.myplayList.MyPlaylistIntent
import com.example.haductrung.myplayList.MyPlaylistScreen
import com.example.haductrung.myplayList.MyPlaylistViewModel
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
            // MyPlaylistViewModel sẽ quản lý danh sách các playlist người dùng tạo
            val viewModel: MyPlaylistViewModel = viewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            // Xử lý sự kiện điều hướng khi người dùng nhấn vào một playlist cụ thể
            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when (event) {
                        is MyPlaylistEvent.NavigateToPlaylistDetail -> {
                            // Điều hướng đến màn hình chi tiết của playlist đó
                            // navController.navigate(...)
                        }
                    }
                }
            }

            // Hiển thị màn hình quản lý playlist
            MyPlaylistScreen(
                state = state,
                onIntent = viewModel::processIntent
            )
        }
        composable<Library> {
            val context = LocalContext.current
            val viewModel: LibraryViewModel = viewModel {
                LibraryViewModel(LibraryRepository(context), context.applicationContext)
            }
            val state by viewModel.state.collectAsStateWithLifecycle()


            var hasPermission by remember { mutableStateOf() }
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                hasPermission = isGranted
                if (isGranted) {
                    viewModel.processIntent(LibraryIntent.CheckAndLoadSongs)
                }
            }

            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when (event) {
                        is LibraryEvent.RequestPermission -> {
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.processIntent(LibraryIntent.CheckAndLoadSongs)
            }
            if (hasPermission) {
                LibraryScreen(
                    state = state,
                    onIntent = viewModel::processIntent
                )
            } else {
            }
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