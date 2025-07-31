package com.example.haductrung.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel(){
    //giuwxc state cho UI theo dõi
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
    // shareflow gửi các even 1 lần
    private val _event = MutableSharedFlow<ProfileEvent>()
    val event = _event.asSharedFlow()
    // nhận và xử lí intent
    fun processIntent(intent: ProfileIntent){
        when(intent){
            is ProfileIntent.onNameChange->{
                _state.update { it.copy(name = intent.newName, nameError = null) }
            }
            is ProfileIntent.onPhoneChange->{
                _state.update { it.copy(phone=intent.newPhone, phoneError = null) }
            }
            is ProfileIntent.onUniversityChange->{
                _state.update { it.copy(university = intent.newUniversity, universityError = null) }
            }
            is ProfileIntent.onDescriptionChange->{
                _state.update { it.copy(description = intent.newDescription) }
            }
            is ProfileIntent.onAvatarClick->{
                viewModelScope.launch { _event.emit(ProfileEvent.OpenImagePicker) }
            }
            is ProfileIntent.onEditClick->{
                _state.update { it.copy(isEditing = true) }
            }
            is ProfileIntent.onSubmitClick->{
                submitProfile()
            }
            is ProfileIntent.OnBack -> {
                viewModelScope.launch { _event.emit(ProfileEvent.NavigateBack) }
            }
            is ProfileIntent.OnAvatarChange -> {
                _state.update { it.copy(imageUri = intent.newUri) }
            }


        }

    }
    private fun submitProfile() {

        val currentState = _state.value
        _state.update { it.copy(nameError = null, phoneError = null, universityError = null) }

        var isValid = true
        var finalState = _state.value

        if (!currentState.name.matches(Regex("^[a-zA-Z\\s]*$"))) {
            isValid = false
            finalState = finalState.copy(nameError = "invalid format")
        }
        if (!currentState.phone.matches(Regex("^0\\d{9}$"))) {
            isValid = false
            finalState = finalState.copy(phoneError = "invalid format")
        }
        if (!currentState.university.matches(Regex("^[a-zA-Z\\s]*$"))) {
            isValid = false
            finalState = finalState.copy(universityError = "invalid format")
        }

        if (isValid) {
            _state.update { it.copy(isEditing = false) }
            viewModelScope.launch { _event.emit(ProfileEvent.ShowSuccessPopup) }
        } else {
            _state.value = finalState
        }
    }

}