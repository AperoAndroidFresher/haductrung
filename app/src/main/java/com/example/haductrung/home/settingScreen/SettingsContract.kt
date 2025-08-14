package com.example.haductrung.home.settingScreen

data class Language(val code: String, val name: String)
data class SettingsState(
    val savedLanguage: Language = Language("en", "English"),
    val selectedLanguage: Language = Language("en", "English"),
    val isDropdownExpanded: Boolean = false
)
sealed interface SettingsIntent {
    data object OnBackClick : SettingsIntent
    data object OnSaveClick : SettingsIntent
    data object OnLanguageRowClick : SettingsIntent
    data class OnLanguageSelected(val language: Language) : SettingsIntent
    data object OnDismissDropdown : SettingsIntent
}
sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object RecreateActivity : SettingsEvent

}
