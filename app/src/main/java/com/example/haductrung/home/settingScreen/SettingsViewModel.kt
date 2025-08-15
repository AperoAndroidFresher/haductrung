package com.example.haductrung.home.settingScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()

    private val settingsDataStore = SettingsDataStore(application)

    private val availableLanguages = listOf(
        Language("en", "English"),
        Language("vi", "Tiếng Việt"),
        Language("ko", "한국어"),
        Language("fr", "Français")
    )
    init {
        settingsDataStore.getLanguage.onEach { savedLanguageCode ->
            val savedLanguage = availableLanguages.find { it.code == savedLanguageCode }
                ?: availableLanguages.first()
            _state.update {
                it.copy(
                    savedLanguage = savedLanguage,
                    selectedLanguage = savedLanguage
                )
            }
        }.launchIn(viewModelScope)
    }
    fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OnLanguageRowClick -> {
                _state.update { it.copy(isDropdownExpanded = true) }
            }
            is SettingsIntent.OnDismissDropdown -> {
                _state.update { it.copy(isDropdownExpanded = false) }
            }
            is SettingsIntent.OnLanguageSelected -> {
                _state.update {
                    it.copy(
                        selectedLanguage = intent.language,
                        isDropdownExpanded = false
                    )
                }
            }
            is SettingsIntent.OnSaveClick -> {
                viewModelScope.launch {
                    val languageToSave = state.value.selectedLanguage
                    settingsDataStore.saveLanguage(languageToSave.code)

                    _state.update { it.copy(savedLanguage = languageToSave) }
                    _event.emit(SettingsEvent.RecreateActivity)
                }
            }
            is SettingsIntent.OnBackClick -> {
                viewModelScope.launch {
                    _event.emit(SettingsEvent.NavigateBack)
                }
            }
        }
    }
}