package com.example.haductrung

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.haductrung.profile.ProfileScreen
//import com.example.haductrung.signup_login.AuthScreen
import com.example.haductrung.signup_login.LoginScreen
import com.example.haductrung.signup_login.SignupScreen
import com.example.haductrung.signup_login.WelcomeScreen
import com.example.haductrung.song.Song
import com.example.haductrung.song.SongScreen
import com.example.haductrung.ui.theme.HaductrungTheme
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay


sealed interface Screen {
    data object Welcome : Screen
    data object Login : Screen
    data object SignUp : Screen
    data object Home : Screen
    data object Profile : Screen
    data object Library : Screen
    data object Playlist : Screen

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HaductrungTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Welcome) }
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
    var isGridView by remember { mutableStateOf(false) }
    var songWithMenu  by remember { mutableStateOf<Int?>(null) }
    var isSortMode by remember { mutableStateOf(false) }
    val songList = remember {
        mutableListOf(
            Song(1,"Rainy days", "Moody,", "04:30", R.drawable.grainydays),
            Song(2,"Cofffee", "Kainbeats", "04:30", R.drawable.cofee,),
            Song(3,"Rainfdrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song(4,"Tokydo", "SmYang", "04:02", R.drawable.tokyo),
            Song(5,"Lulflaby", "Iamfinenow", "04:02", R.drawable.lulabby),
            Song(6,"Raindsy dayss", "Moody,", "04:30", R.drawable.grainydays),
            Song(7,"Rainy days", "Moody,", "04:30", R.drawable.grainydays),
            Song(8,"Cofffee", "Kainbeats", "04:30", R.drawable.cofee,),
            Song(9,"Rainfdrops", "Rainyyxx", "00:30", R.drawable.raindrop),
            Song(10,"Tokydo", "SmYang", "04:02", R.drawable.tokyo),
            Song(11,"Lulflaby", "Iamfinenow", "04:02", R.drawable.lulabby),
        )
    }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var universisyError by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var popup by remember { mutableStateOf(false) }
    var universityError by remember { mutableStateOf<String?>(null) }
    val activity = LocalActivity.current as? Activity

    NavDisplay(
        backStack = backStack,
        onBack = {
            when (backStack.lastOrNull()) {
                is Screen.SignUp,
                is Screen.Profile -> {
                    if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
                }

                is Screen.Home,
                is Screen.Library,
                is Screen.Playlist -> {
                    backStack.clear()
                    activity?.finish()
                }

                else -> {
                    if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
                }
            }
        }
    ) { screen ->
        BackHandler {
            when (backStack.lastOrNull()) {
                is Screen.Home,
                is Screen.Library,
                is Screen.Playlist -> {
                    backStack.clear()
                    activity?.finish()
                }

                else -> {
                    if (backStack.isNotEmpty()) {
                        backStack.removeAt(backStack.lastIndex)
                    } else {
                        activity?.finish()
                    }
                }
            }
        }
        when (screen) {
            is Screen.Welcome -> WelcomeScreen(
                onTimeout = {
                    backStack.clear()
                    backStack.add(Screen.Login)
                }
            )

            is Screen.Login -> LoginScreen(
                username = username,
                onUsernameChange = { username = it },

                password = password,
                onPasswordChange = { password = it },

                isChecked = rememberMe,
                onCheckedChange = { rememberMe = it },

                onLoginClick = {
                    if (username != "" && password != "") {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    }
                },//trick
                SetNews = {
                    username = ""
                    password = ""
                    confirmPassword = ""
                    email = ""
                    usernameError = null
                    passwordError = null
                    confirmPasswordError = null
                    emailError = null
                    isPasswordVisible = false
                    backStack.add(Screen.SignUp)
                },

                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
            )

            is Screen.SignUp -> SignupScreen(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                email = email,
                usernameError = usernameError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                emailError = emailError,
                onEmailChange = { email = it },
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
                        backStack.add(Screen.Login)
                    }

                },
                onBack = { backStack.removeLastOrNull() },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                isConfirmPasswordVisible = isConfirmPasswordVisible,
                onToggleConfirmPasswordVisibility = {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                }
            )

            is Screen.Home -> Home(
                onLibraryButtonClick = { backStack.add(Screen.Library) },
                onMyPlaylistButtonClick = { backStack.add(Screen.Playlist) },
                onHomeButtonClick = {backStack.add(Screen.Home)},
                onProfileIconClick = {backStack.add(Screen.Profile)}
            )

            is Screen.Profile -> ProfileScreen(
                name = name,
                phone = phone,
                university = university,
                description = description,
                isEditing = isEditing,
                showPopup = popup,
                nameError = nameError,
                phoneError = phoneError,
                universityError = universityError,
                onNameChange = { name = it },
                onPhoneChange = { phone = it },
                onUniversityChange = { university = it },
                onDescriptionChange = { description = it },
                onEditClick = { isEditing = true },
                onDismissPopup = { popup = false },
                onSubmitClick = {
                    nameError = null
                    phoneError = null
                    universisyError = null
                    var isValid = true
                    if (!name.matches(Regex("^[a-zA-Z\\s]*$"))) {
                        isValid = false
                        nameError= "invalid format"
                    }
                    if (!phone.matches(Regex("^0\\d{9}$"))) {
                        isValid = false
                        phoneError= "invalid format"
                    }
                    if (!university.matches(Regex("^[a-zA-Z\\s]*$"))) {
                        isValid = false
                        universisyError= "invalid format"
                    }
                    if (isValid) {
                        isEditing = false
                        popup = true
                    }
                },
                onBack = { backStack.removeLastOrNull() }
            )

            is Screen.Library -> LibraryScreen(

            )

            is Screen.Playlist -> SongScreen(
                songList = songList,
                isGridView = isGridView,
                isSortMode = isSortMode,
                songWithMenu = songWithMenu,
                onToggleViewClick = { isGridView = !isGridView },
                onToggleSortClick = {isSortMode = !isSortMode
                },
                onMoreClick = { song1 -> songWithMenu = song1.id },
                onDismissMenu = { songWithMenu = null },
                onDeleteClick = { song1 ->
                    songList.removeIf{it.id==song1.id}
                    songWithMenu = null
                },
                onNavigateToProfile = { backStack.add(Screen.Profile) }
            )
        }
    }
}
@Composable
fun NavDisplay(
    backStack: SnapshotStateList<Screen>,
    onBack: () -> Unit,
    entryProvider: @Composable (screen: Screen) -> Unit
) {
    val currentScreen = backStack.lastOrNull()
    if (currentScreen != null) {
        Crossfade(targetState = currentScreen, label = "navigation") { screen ->
            entryProvider(screen)
        }
    }
}
