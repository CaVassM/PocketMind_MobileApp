package com.example.ta_movil.ViewModels.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.ta_movil.ViewModels.userLogin.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthSharedViewModel : ViewModel() {
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    fun showLogoutDialog() {
        _showLogoutDialog.value = true
    }

    fun hideLogoutDialog() {
        _showLogoutDialog.value = false
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            val authViewModel = AuthViewModel()
            authViewModel.logout(navController)
            // Ocultar el diálogo después de la navegación
            hideLogoutDialog()
        }
    }
}
