package com.example.ta_movil.Model.pagesInit

// Claramente, se puede guardar el password así en duro. Solo se retiene lo ingresado por el usuario desde android pero, no desde la database.
// Ojo, se retiene states de TODO EL FLUJO DE REGISTER
data class registerState(
    // Pantalla create_account
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val buttonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",

    // Pantalla verify_identity
    val errorMessageVerify: String = ""

)