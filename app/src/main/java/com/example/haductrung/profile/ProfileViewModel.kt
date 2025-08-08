package com.example.haductrung.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.haductrung.database.entity.UserEntity
import com.example.haductrung.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import com.example.haductrung.signup_login.SessionManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class ProfileViewModel(
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<ProfileEvent>()
    val event = _event.asSharedFlow()

    private var currentUser: UserEntity? = null

    init {
        SessionManager.currentUserId
            .onEach { userId ->
                if (userId != null) {
                    loadUserProfile(userId)
                } else {
                    _state.value = ProfileState()
                    currentUser = null
                }
            }
            .launchIn(viewModelScope)
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

    fun processIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnNameChange -> {
                _state.update { it.copy(name = intent.newName, nameError = null) }
            }

            is ProfileIntent.OnPhOneChange -> {
                _state.update { it.copy(phone = intent.newPhOne, phoneError = null) }
            }

            is ProfileIntent.OnUniversityChange -> {
                _state.update { it.copy(university = intent.newUniversity, universityError = null) }
            }

            is ProfileIntent.OnDescriptiOnChange -> {
                _state.update { it.copy(description = intent.newDescriptiOn) }
            }

            is ProfileIntent.OnAvatarClick -> {
                viewModelScope.launch { _event.emit(ProfileEvent.OpenImagePicker) }
            }

            is ProfileIntent.OnEditClick -> {
                _state.update { it.copy(isEditing = true) }
            }

            is ProfileIntent.OnSubmitClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    submitProfile()
                }
            }

            is ProfileIntent.OnBack -> {
                viewModelScope.launch { _event.emit(ProfileEvent.NavigateBack) }
            }

            is ProfileIntent.OnAvatarChange -> {
                intent.newUri?.let { uri ->
                    saveImageToInternalStorage(uri)
                }
            }
        }

    }

    private fun saveImageToInternalStorage(tempUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val inputStream: InputStream? = context.contentResolver.openInputStream(tempUri)

            val userId = currentUser?.userId ?: System.currentTimeMillis()
            val newFile = File(context.filesDir, "avatar_$userId.jpg")
            val outputStream = FileOutputStream(newFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            val permanentUri = newFile.toUri()
            _state.update { it.copy(imageUri = permanentUri) }
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

            viewModelScope.launch(Dispatchers.Main) {
                _state.update { it.copy(isEditing = false) }
                _event.emit(ProfileEvent.ShowSuccessPopup)
            }
        }
    }
}

