package com.example.haductrung.profile

import android.net.Uri
//state
data class ProfileState(
    val name: String = "",
    val phone: String = "",
    val university: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val isEditing: Boolean = false,
    val nameError: String? = null,
    val phoneError: String? = null,
    val universityError: String? = null,
    val isLoading: Boolean = true
)

//intent
sealed interface ProfileIntent{
    data class OnNameChange(val newName: String) : ProfileIntent
    data class OnPhOneChange(val newPhOne: String) : ProfileIntent
    data class OnUniversityChange(val newUniversity: String) : ProfileIntent
    data class OnDescriptiOnChange(val newDescriptiOn: String) : ProfileIntent
    data object OnEditClick : ProfileIntent
    data object OnSubmitClick : ProfileIntent
    data object OnAvatarClick : ProfileIntent
    data class OnAvatarChange(val newUri: Uri?) : ProfileIntent
    data object OnBack : ProfileIntent

}
//event
sealed interface ProfileEvent{
    data object ShowSuccessPopup : ProfileEvent
    data object OpenImagePicker : ProfileEvent
    data object NavigateBack : ProfileEvent
}