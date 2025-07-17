package com.example.ta_movil.ViewModels.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
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
    val showDeleteAccountDialog: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val dashboardViewModel: DashboardViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    // Exponer el DashboardViewModel para la navegación
    val dashboardViewModelPublic: DashboardViewModel get() = this.dashboardViewModel

    val currentScreen : Screen get() = dashboardViewModel.currentScreen


    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        _uiState.value = _uiState.value.copy(
            user = currentUser,
            displayName = currentUser?.displayName ?: "",
            email = currentUser?.email ?: "",
            phoneNumber = currentUser?.phoneNumber ?: "",
            isLoading = false
        )
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditMode = !_uiState.value.isEditMode
        )
    }

    fun updateDisplayName(name: String) {
        _uiState.value = _uiState.value.copy(
            displayName = name
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email
        )
    }

    fun updatePhoneNumber(phone: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phone
        )
    }

    fun updateSelectedCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
    }

    fun saveProfileChanges() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: throw Exception("Usuario no encontrado")
                
                // Crear UserProfileChangeRequest con los cambios
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(_uiState.value.displayName)
                    .build()

                // Actualizar el perfil
                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Actualizar el estado
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isEditMode = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = task.exception?.message ?: "Error al actualizar el perfil"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al actualizar el perfil"
                )
            }
        }
    }

    fun showDeleteAccountDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteAccountDialog = true
        )
    }

    fun hideDeleteAccountDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteAccountDialog = false
        )
    }

    fun deleteAccount() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: throw Exception("Usuario no encontrado")
                user.delete()
                auth.signOut()
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al eliminar la cuenta"
                )
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}