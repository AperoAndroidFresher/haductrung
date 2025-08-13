package com.example.haductrung

import android.Manifest
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.navigation.compose.dialog
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.haductrung.home.Home
import com.example.haductrung.home.HomeEvent
import com.example.haductrung.home.HomeViewModel
import com.example.haductrung.library.LibraryEvent
import com.example.haductrung.library.LibraryIntent
import com.example.haductrung.repository.LibraryRepository
import com.example.haductrung.library.LibraryScreen
import com.example.haductrung.library.LibraryViewModel
import com.example.haductrung.library.addtoplaylist.AddToPlaylistEvent
import com.example.haductrung.library.addtoplaylist.AddToPlaylistScreen
import com.example.haductrung.library.addtoplaylist.AddToPlaylistViewModel
import com.example.haductrung.my_playlist.MyPlaylistScreen
import com.example.haductrung.my_playlist.PlaylistEvent
import com.example.haductrung.my_playlist.PlaylistViewModel
import com.example.haductrung.my_playlist.playlistdetail.PlaylistDetailScreen
import com.example.haductrung.my_playlist.playlistdetail.PlaylistDetailViewModel
import com.example.haductrung.repository.PlaylistRepository
import com.example.haductrung.profile.ProfileEvent
import com.example.haductrung.profile.ProfileIntent
import com.example.haductrung.profile.ProfileScreen
import com.example.haductrung.profile.ProfileViewModel
import com.example.haductrung.signup_login.loginScreen.LoginEvent
import com.example.haductrung.signup_login.loginScreen.LoginScreen
import com.example.haductrung.signup_login.loginScreen.LoginViewModel
import com.example.haductrung.signup_login.signUpScreen.SignUpEvent
import com.example.haductrung.signup_login.signUpScreen.SignUpViewModel
import com.example.haductrung.signup_login.signUpScreen.SignupScreen
import com.example.haductrung.signup_login.minicomposale.WelcomeScreen
import com.example.haductrung.ui.theme.HaductrungTheme
import kotlinx.serialization.Serializable
import com.example.haductrung.database.AppDatabase
import com.example.haductrung.home.bottomHomeBar.BottomHomeBar
import com.example.haductrung.home.remote.detailScreenRemote.TopAlbumsScreen
import com.example.haductrung.home.remote.detailScreenRemote.TopArtistsScreen
import com.example.haductrung.home.remote.detailScreenRemote.TopTracksScreen
import com.example.haductrung.musicPlayerBar.BottomPlayerScreen
import com.example.haductrung.musicPlayerBar.PlayerDetailScreen
import com.example.haductrung.musicPlayerBar.PlayerUiIntent
import com.example.haductrung.musicPlayerBar.PlayerUiState
import com.example.haductrung.musicPlayerBar.PlayerViewModel
import com.example.haductrung.playback.PlayerManager
import com.example.haductrung.playback.PlayerViewModelFactory
import com.example.haductrung.repository.SongRepository
import com.example.haductrung.repository.UserRepository
import com.example.haductrung.signup_login.SessionManager


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

@Serializable
object TopAlbums

@Serializable
object TopArtists

@Serializable
object TopTracks


class MainActivity : ComponentActivity() {
    private val playerViewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(application)
    }
    private fun handleIntent(intent: Intent) {
        if (intent.action == "ACTION_OPEN_PLAYER_DETAIL") {
            playerViewModel.processIntent(PlayerUiIntent.OpenPlayerDetail)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        PlayerManager.viewModel = playerViewModel
        playerViewModel.checkAndRestoreState()
        handleIntent(intent)
        setContent {
            HaductrungTheme {
                    val playerState by playerViewModel.state.collectAsStateWithLifecycle()
                    AppNavigation(playerViewModel = playerViewModel, playerState = playerState)
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }
    override fun onStop() {
        super.onStop()
        playerViewModel.processIntent(PlayerUiIntent.AppEnteredBackground)
    }

}

@Composable
fun AppNavigation(playerViewModel: PlayerViewModel, playerState: PlayerUiState) {
    val navController = rememberNavController()
    val startDestination = if (SessionManager.getLoggedInUserId() != null) {
        Home
    } else {
        Welcome
    }
    val screensWithoutPlayer = listOf(Welcome::class, Login::class, SignUp::class, Profile::class)
        .mapNotNull { it.qualifiedName }

    val screensWithBottomNav = listOf(Home::class, Library::class, Playlist::class, PlaylistDetail::class)
        .mapNotNull { it.qualifiedName }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomNav = screensWithBottomNav.any { currentRoute?.startsWith(it) == true }
    val showPlayer = currentRoute !in screensWithoutPlayer && playerState.currentPlayingSong != null
    LaunchedEffect(currentRoute) {
        playerViewModel.processIntent(PlayerUiIntent.ScreenChanged(currentRoute))
    }
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
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
            composable<Login> { backStackEntry ->
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
                val database = AppDatabase.getDatabase(LocalContext.current)
                val userDao = database.userDAO()
                val userRepository = UserRepository(userDao)
                val context = LocalContext.current

                val navBackStackEntry = navController.getBackStackEntry(Home::class)
                val viewModelh: HomeViewModel = viewModel(
                    viewModelStoreOwner = navBackStackEntry,
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                                return HomeViewModel(
                                    userRepository = userRepository,
                                    application = context.applicationContext as Application as MyApplication
                                ) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }
                )
                val state by viewModelh.state.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) {
                    viewModelh.event.collect { event ->
                        when (event) {
                            is HomeEvent.NavigateToProfile -> navController.navigate(Profile)
                            is HomeEvent.NavigateToLibrary -> navController.navigate(Library)
                            is HomeEvent.NavigateToPlaylist -> navController.navigate(Playlist)
                            is HomeEvent.NavigateToTopAlbums -> navController.navigate(TopAlbums)
                            is HomeEvent.NavigateToTopArtists -> navController.navigate(TopArtists)
                            is HomeEvent.NavigateToTopTracks -> navController.navigate(TopTracks)
                        }
                    }
                }

                Home(
                    state = state,
                    onIntent = viewModelh::processIntent,
                )
            }
            composable<TopAlbums> {
                // Lấy lại ViewModel đã được chia sẻ từ Home
                val navBackStackEntry = navController.getBackStackEntry(Home::class)
                val viewModelh: HomeViewModel = viewModel(viewModelStoreOwner = navBackStackEntry)
                val state by viewModelh.state.collectAsStateWithLifecycle()

                // Gọi màn hình mới và truyền dữ liệu vào
                TopAlbumsScreen(
                    albums = state.topAlbums, // Truyền toàn bộ danh sách album
                    onBackClick = {
                        navController.popBackStack() // Xử lý khi nhấn nút back
                    }
                )
            }
            composable<TopArtists> {
                // Lấy lại ViewModel đã được chia sẻ từ Home
                val navBackStackEntry = navController.getBackStackEntry(Home::class)
                val viewModelh: HomeViewModel = viewModel(viewModelStoreOwner = navBackStackEntry)
                val state by viewModelh.state.collectAsStateWithLifecycle()

                // Gọi màn hình mới và truyền dữ liệu vào
                TopArtistsScreen(
                    artists = state.topArtists, // Truyền toàn bộ danh sách nghệ sĩ
                    onBackClick = {
                        navController.popBackStack() // Xử lý khi nhấn nút back
                    }
                )
            }
            composable<TopTracks> {
                // Lấy lại ViewModel đã được chia sẻ từ Home
                val navBackStackEntry = navController.getBackStackEntry(Home::class)
                val viewModelh: HomeViewModel = viewModel(viewModelStoreOwner = navBackStackEntry)
                val state by viewModelh.state.collectAsStateWithLifecycle()

                // Gọi màn hình mới và truyền dữ liệu vào
                TopTracksScreen(
                    tracks = state.topTracks, // Truyền toàn bộ danh sách track
                    onBackClick = {
                        navController.popBackStack() // Xử lý khi nhấn nút back
                    }
                )
            }
            composable<Library> {
                val context = LocalContext.current

                val database = AppDatabase.getDatabase(context)
                val songDao = database.songDao()
                val songRepository = SongRepository(songDao)
                val mediaStoreScanner = LibraryRepository(context)

                val viewModelPL: LibraryViewModel = viewModel {
                    LibraryViewModel(
                        songRepository = songRepository,
                        mediaStoreScanner = mediaStoreScanner,
                        applicationContext = context.applicationContext
                    )
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
                    onIntent = viewModelPL::processIntent,
                    onPlaySong = { song ->
                        playerViewModel.processIntent(PlayerUiIntent.PlaySong(song, null))
                    }
                )
            }
            dialog<AddToPlaylist> { backStackEntry ->
                val context = LocalContext.current

                val database = AppDatabase.getDatabase(context)
                val playlistDao = database.playlistDao()
                val playlistRepository = PlaylistRepository(playlistDao)

                val viewModel: AddToPlaylistViewModel = viewModel {
                    AddToPlaylistViewModel(
                        playlistRepository = playlistRepository,
                        savedStateHandle = backStackEntry.savedStateHandle
                    )
                }
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.event.collect { event ->
                        when (event) {
                            is AddToPlaylistEvent.GoBack -> navController.popBackStack()
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
                    onIntent = viewModel::processIntent,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Playlist> {
                val database = AppDatabase.getDatabase(LocalContext.current)
                val playlistDao = database.playlistDao()

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


                val database = AppDatabase.getDatabase(context)

                val playlistDao = database.playlistDao()
                val playlistRepository = PlaylistRepository(playlistDao)

                val songDao = database.songDao()
                val songRepository = SongRepository(songDao)
                val viewModelDetail: PlaylistDetailViewModel = viewModel {
                    PlaylistDetailViewModel(
                        savedStateHandle = backStackEntry.savedStateHandle,
                        playlistRepository = playlistRepository,
                        songRepository = songRepository
                    )
                }
                val state by viewModelDetail.state.collectAsStateWithLifecycle()

                PlaylistDetailScreen(
                    state = state,
                    onIntent = viewModelDetail::processIntent,
                    onPlaySong = { song, playlist ->
                        playerViewModel.processIntent(PlayerUiIntent.PlaySong(song, playlist))
                    }
                )
            }
            composable<Profile> {
                val database = AppDatabase.getDatabase(LocalContext.current)
                val userDao = database.userDAO()
                val userRepository = UserRepository(userDao)
                val context = LocalContext.current

                val viewModel: ProfileViewModel =
                    viewModel(factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProfileViewModel(
                                userRepository = userRepository,
                                application = context.applicationContext as Application
                            ) as T
                        }
                    })

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
                            is ProfileEvent.NavigateToLogin -> {
                                navController.navigate(Login) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                }
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
        Box(modifier = Modifier.fillMaxSize(),) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                if (showPlayer) {
                    BottomPlayerScreen(
                        state = playerState,
                        onIntent = playerViewModel::processIntent
                    )
                }
                if (showBottomNav) {
                    BottomHomeBar(
                        navController = navController,
                    )
                }
            }
            if (playerState.isDetailScreenVisible) {
                PlayerDetailScreen(
                    state = playerState,
                    onIntent = playerViewModel::processIntent,
                )
            }
        }
    }
}