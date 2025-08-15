package com.example.haductrung.home.settingScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.haductrung.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableLanguages = listOf(
        Language("en", stringResource(id = R.string.english)),
        Language("vi", stringResource(id = R.string.vietnamese)),
        Language("ko", stringResource(id = R.string.korean)),
        Language("fr", stringResource(id = R.string.french))
    )

    val hasChanges = state.savedLanguage != state.selectedLanguage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(SettingsIntent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Chỉ hiển thị icon tick V khi có thay đổi
                    if (hasChanges) {
                        IconButton(onClick = { onIntent(SettingsIntent.OnSaveClick) }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues).padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIntent(SettingsIntent.OnLanguageRowClick) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.language),
                    contentDescription = stringResource(id = R.string.language),
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.language),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    Text(
                        text = state.selectedLanguage.name,
                        color = Color.Gray
                    )
                    DropdownMenu(
                        expanded = state.isDropdownExpanded,
                        onDismissRequest = { onIntent(SettingsIntent.OnDismissDropdown) },
                        modifier = Modifier.background(Color.DarkGray)
                    ) {
                        availableLanguages.forEach { language ->
                            DropdownMenuItem(
                                text = {
                                    Text(language.name, color = Color.White)
                                },
                                onClick = { onIntent(SettingsIntent.OnLanguageSelected(language)) }
                            )
                        }
                    }
                }
            }
        }
    }
}