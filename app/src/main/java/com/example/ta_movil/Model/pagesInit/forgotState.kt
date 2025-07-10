package com.example.ta_movil.Model.pagesInit

data class forgotState(
    val correo: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val errorLabel: Boolean = false,
    val buttonEnabled: Boolean = false
)