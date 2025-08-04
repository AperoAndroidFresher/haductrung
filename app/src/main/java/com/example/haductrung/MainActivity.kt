package com.example.haductrung

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.navigation.compose.dialog
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.haductrung.library.LibraryRepository
import com.example.haductrung.library.LibraryScreen
import com.example.haductrung.library.LibraryViewModel
import com.example.haductrung.library.minicomposable.addtoplaylist.AddToPlaylistEvent
import com.example.haductrung.library.minicomposable.addtoplaylist.AddToPlaylistScreen
import com.example.haductrung.library.minicomposable.addtoplaylist.AddToPlaylistViewModel
import com.example.haductrung.my_playlist.MyPlaylistScreen
import com.example.haductrung.my_playlist.PlaylistEvent
import com.example.haductrung.my_playlist.PlaylistViewModel
import com.example.haductrung.my_playlist.playlistdetail.PlaylistDetailScreen
import com.example.haductrung.my_playlist.playlistdetail.PlaylistDetailViewModel
import com.example.haductrung.my_playlist.playlistdetail.PlaylistRepository
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
import com.example.haductrung.ui.theme.HaductrungTheme
import kotlinx.serialization.Serializable
import com.example.haductrung.database.AppDatabase
import com.example.haductrung.user.UserRepository

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

@Serializable
data class AddToPlaylist(val songId: Int)


@Serializable
data class PlaylistDetail(val playlistId: String)


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

        composable<Login> {backStackEntry ->
            val database = AppDatabase.getDatabase(LocalContext.current)
            val userDao = database.userDAO()
            val userRepository = UserRepository(userDao)

            val viewModellg: LoginViewModel = viewModel {
                LoginViewModel(
                    userRepository = userRepository,
                    savedStateHandle = backStackEntry.savedStateHandle
                )
            }
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
            val database = AppDatabase.getDatabase(LocalContext.current)
            val userDao = database.userDAO()
            val userRepository = UserRepository(userDao)

            val viewModelSU: SignUpViewModel = viewModel {
                SignUpViewModel(userRepository = userRepository)
            }
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
        composable<Library> {
            val context = LocalContext.current
            val viewModelPL: LibraryViewModel = viewModel {
                LibraryViewModel(LibraryRepository(context), context.applicationContext)
            }
            val state by viewModelPL.state.collectAsStateWithLifecycle()
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    viewModelPL.processIntent(LibraryIntent.CheckAndLoadSongs)
                }
            }
            LaunchedEffect(Unit) {
                viewModelPL.event.collect { event ->
                    when (event) {
                        is LibraryEvent.RequestPermission -> {
                            val permission =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Manifest.permission.READ_MEDIA_AUDIO
                                } else {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                }
                            permissionLauncher.launch(permission)
                        }

                        is LibraryEvent.NavigateToAddToPlaylistScreen -> {
                            navController.navigate(AddToPlaylist(songId = event.song.id))
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                viewModelPL.processIntent(LibraryIntent.CheckAndLoadSongs)
            }
            LibraryScreen(
                state = state,
                onIntent = viewModelPL::processIntent
            )
        }
        dialog<AddToPlaylist> { backStackEntry ->
            val context = LocalContext.current

            val database = AppDatabase.getDatabase(context)
            val playlistDao = database.playlistDao()
            val playlistRepository = PlaylistRepository(playlistDao)

            val viewModelAdd: AddToPlaylistViewModel = viewModel {
                AddToPlaylistViewModel(
                    savedStateHandle = backStackEntry.savedStateHandle,
                    playlistRepository = playlistRepository // Lấy repository
                )
            }
            val state by viewModelAdd.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModelAdd.event.collect { event ->
                    when (event) {
                        is AddToPlaylistEvent.GoBack -> {
                            navController.popBackStack()
                        }

                        is AddToPlaylistEvent.NavigateToMyPlaylist -> {
                            navController.navigate(Playlist) {
                                popUpTo<AddToPlaylist> { inclusive = true }
                            }
                        }

                        is AddToPlaylistEvent.ShowSuccessMessage -> {
                            Toast.makeText(
                                context,
                                "Added to ${event.playlistName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            AddToPlaylistScreen(
                state = state,
                onIntent = viewModelAdd::processIntent,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Playlist> {
            val database = AppDatabase.getDatabase(LocalContext.current)
            // lấy DAO
            val playlistDao = database.playlistDao()
            // tạo Repository từ DAO
            val playlistRepository = PlaylistRepository(playlistDao)
            val viewModell: PlaylistViewModel = viewModel {
                PlaylistViewModel(playlistRepository = playlistRepository)
            }

            val state by viewModell.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModell.event.collect { event ->
                    when (event) {
                        is PlaylistEvent.NavigateToPlaylistDetail -> {
                            navController.navigate(PlaylistDetail(playlistId = event.playlistId))
                        }
                    }
                }
            }

            MyPlaylistScreen(
                state = state,
                onIntent = viewModell::processIntent
            )
        }
        composable<PlaylistDetail> { backStackEntry ->
            val context = LocalContext.current
//connect db
            val database = AppDatabase.getDatabase(context)
            val playlistDao = database.playlistDao()
            val playlistRepository = PlaylistRepository(playlistDao)

            val libraryRepository = LibraryRepository(context)
            val viewModelDetail: PlaylistDetailViewModel = viewModel {
                PlaylistDetailViewModel(
                    savedStateHandle = backStackEntry.savedStateHandle,
                    playlistRepository = playlistRepository,
                    libraryRepository = libraryRepository
                )
            }
            val state by viewModelDetail.state.collectAsStateWithLifecycle()

            PlaylistDetailScreen(
                state = state,
                onIntent = viewModelDetail::processIntent
            )
        }
        composable<Profile> {
            val database = AppDatabase.getDatabase(LocalContext.current)
            val userDao = database.userDAO()
            val userRepository = UserRepository(userDao)

            val viewModel: ProfileViewModel = viewModel {
                ProfileViewModel(userRepository = userRepository)
            }
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