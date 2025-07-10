package com.example.ta_movil.Model.pagesInit

// Manejará states de la pantalla Login
data class LoginState(
    val isLoginEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val email: String = "",
    val password: String = "",
    val errorLabel: Boolean = false
)