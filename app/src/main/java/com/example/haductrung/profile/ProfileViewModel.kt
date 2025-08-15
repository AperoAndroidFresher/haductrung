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
                    // Bắt đầu lắng nghe sự thay đổi của user
                    userRepository.findUserById(userId)?.collect { userEntity ->

                        currentUser = userEntity
                        userEntity?.let { user ->
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
                } else {
                    _state.value = ProfileState()
                    currentUser = null
                }
            }
            .launchIn(viewModelScope)
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
                _state.update { it.copy(imageUri = intent.newUri) }
            }
            is ProfileIntent.OnLogoutClick -> {
                viewModelScope.launch { SessionManager.logout()
                    _event.emit(ProfileEvent.NavigateToLogin)
                }
            }
        }

    }

    private fun saveImageToInternalStorage(tempUri: Uri): Uri? {
        val context = getApplication<Application>().applicationContext
        val inputStream: InputStream? = try {
            context.contentResolver.openInputStream(tempUri)
        } catch (e: Exception) {
            return null
        }

        val userId = currentUser?.userId ?: System.currentTimeMillis()
        val newFile = File(context.filesDir, "avatar_$userId.jpg")

        // Dùng try-with-resources (use) để đảm bảo stream luôn được đóng
        try {
            FileOutputStream(newFile).use { output ->
                inputStream?.use { input ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            return null
        }

        return newFile.toUri()
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
            var permanentImageUriString: String? = currentUser!!.imageUri
            currentState.imageUri?.let { tempUri ->
                // Nếu uri trong state không phải là uri đã lưu từ trước
                if (tempUri.toString() != currentUser!!.imageUri) {
                    // Lưu ảnh và lấy uri lâu dài
                    val permanentUri = saveImageToInternalStorage(tempUri)
                    permanentImageUriString = permanentUri?.toString()
                }
            }
            val updatedUser = currentUser!!.copy(
                username = currentState.name,
                phone = currentState.phone,
                university = currentState.university,
                description = currentState.description,
                imageUri = permanentImageUriString
            )
            userRepository.updateUser(updatedUser)

            viewModelScope.launch(Dispatchers.Main) {
                _state.update { it.copy(isEditing = false) }
                _event.emit(ProfileEvent.ShowSuccessPopup)
            }
        }
    }
}

