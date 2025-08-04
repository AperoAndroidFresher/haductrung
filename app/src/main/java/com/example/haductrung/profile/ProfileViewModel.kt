package com.example.haductrung.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haductrung.database.entity.UserEntity
import com.example.haductrung.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri


class ProfileViewModel (private val userRepository: UserRepository): ViewModel(){
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<ProfileEvent>()
    val event = _event.asSharedFlow()

    private var currentUser: UserEntity? = null
    init {
        val currentUserId = 1

        loadUserProfile(currentUserId)
    }
    private fun loadUserProfile(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            currentUser = userRepository.findUserById(userId)
            currentUser?.let { user ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = user.username,
                        phone = user.phone ?: "",
                        university = user.university ?: "",
                        description = user.description ?: "",
                        imageUri = user.imageUri?.toUri()
                    )
                }
            }
        }
    }
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
                viewModelScope.launch(Dispatchers.IO) {
                    submitProfile()
                }
            }
            is ProfileIntent.OnBack -> {
                viewModelScope.launch { _event.emit(ProfileEvent.NavigateBack) }
            }
            is ProfileIntent.OnAvatarChange -> {
                _state.update { it.copy(imageUri = intent.newUri) }
            }


        }

    }
    private suspend fun submitProfile() {
        val currentState = _state.value
        var isValid = true

        _state.update { it.copy(nameError = null, phoneError = null, universityError = null) }

        if (!currentState.name.matches(Regex("^[a-zA-Z\\s]*$"))) {
            _state.update { it.copy(nameError = "Invalid format") }
            isValid = false
        }
        if (!currentState.phone.matches(Regex("^0\\d{9}$"))) {
            _state.update { it.copy(phoneError = "Invalid format") }
            isValid = false
        }
        if (!currentState.university.matches(Regex("^[a-zA-Z\\s]*$"))) {
            _state.update { it.copy(universityError = "Invalid format") }
            isValid = false
        }
        if (isValid && currentUser != null) {
            val updatedUser = currentUser!!.copy(
                phone = currentState.phone,
                university = currentState.university,
                description = currentState.description,
                imageUri = currentState.imageUri?.toString()
            )
            userRepository.updateUser(updatedUser)
            _state.update { it.copy(isEditing = false) }
            _event.emit(ProfileEvent.ShowSuccessPopup)
        }
    }

}