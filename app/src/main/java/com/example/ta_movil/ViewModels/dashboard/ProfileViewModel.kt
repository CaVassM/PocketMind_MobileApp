package com.example.ta_movil.ViewModels.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val selectedCurrency: String = "Soles - Perú",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        _uiState.value = _uiState.value.copy(
            user = currentUser,
            displayName = currentUser?.displayName ?: "Carlos Vásquez",
            email = currentUser?.email ?: "brunophani@gmail.com",
            phoneNumber = currentUser?.phoneNumber ?: "+51 943795752"
        )
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditMode = !_uiState.value.isEditMode
        )
    }

    fun updateDisplayName(name: String) {
        _uiState.value = _uiState.value.copy(displayName = name)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePhoneNumber(phone: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phone)
    }

    fun updateSelectedCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Aquí implementarías la lógica para guardar el perfil
                // Por ejemplo, actualizar Firebase Auth profile

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isEditMode = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun showLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = true)
    }

    fun hideLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
    }

    fun showDeleteAccountDialog() {
        _uiState.value = _uiState.value.copy(showDeleteAccountDialog = true)
    }

    fun hideDeleteAccountDialog() {
        _uiState.value = _uiState.value.copy(showDeleteAccountDialog = false)
    }

    fun logout() {
        auth.signOut()
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                auth.currentUser?.delete()
                _uiState.value = _uiState.value.copy(showDeleteAccountDialog = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message,
                    showDeleteAccountDialog = false
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}