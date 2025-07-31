package com.example.haductrung.profile

import android.net.Uri
//state
data class ProfileState(
    val name: String = "",
    val phone: String = "",
    val university: String = "",
    val description: String = "",
    val imageUri:Uri? = null,
    val isEditing: Boolean = false,
    val nameError: String? = null,
    val phoneError: String? = null,
    val universityError: String? = null
)

//intent
sealed interface ProfileIntent{
    data class onNameChange(val newName: String) : ProfileIntent
    data class onPhoneChange(val newPhone: String) : ProfileIntent
    data class onUniversityChange(val newUniversity: String) : ProfileIntent
    data class onDescriptionChange(val newDescription: String) : ProfileIntent
    data object onEditClick : ProfileIntent
    data object onSubmitClick : ProfileIntent
    data object onAvatarClick : ProfileIntent
    data class OnAvatarChange(val newUri: Uri?) : ProfileIntent
    data object OnBack : ProfileIntent

}
//event
sealed interface ProfileEvent{
    data object ShowSuccessPopup : ProfileEvent
    data object OpenImagePicker : ProfileEvent
    data object NavigateBack : ProfileEvent
}