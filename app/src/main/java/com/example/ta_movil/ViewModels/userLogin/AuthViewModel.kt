package com.example.ta_movil.ViewModels.userLogin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ta_movil.Model.pagesInit.LoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

// El viewModel es el intermediario entre el UI y la database.
// Por ahora, la database se enceuntra sin implementar (todo está en firebase)

class AuthViewModel : ViewModel(){

    // Ahora manejaremos el state del login.
    var state by mutableStateOf(LoginState())
        private set

    // Ahora implementaremos los métodos.
    // ButtonEnabled ahí tal que, se verifique cada vez que se cambien los valores para habilitar o no el botón.
    fun onEmailInput(email: String){
        state = state.copy(email = email)
        buttonEnabled()
    }

    fun onPasswordInput(password: String){
        state = state.copy(password = password)
        buttonEnabled()
    }

    fun onLogin(auth: FirebaseAuth, onSuccess: () -> Unit){
        state = state.copy(isLoading = true) // Esto hasta que se termine.
        // Los Tasks de firebase ya manejan errores. No hay necesidad de usar try - catch
        auth.signInWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login exitoso
                    onLoginSuccess()
                    onSuccess() // Navegación
                } else {
                    // Falló el login, task.exception contiene el error
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
                        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
                        else -> "Ocurrió un error. Intente nuevamente."
                    }
                    onLoginError(errorMessage) // Actualiza el UI con el error
                }
            }
    }

    fun createAccount(auth: FirebaseAuth, onSuccess: () -> Unit) {
        state = state.copy(isLoading = true)
        
        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Cuenta creada exitosamente
                    onLoginSuccess()
                    onSuccess() // Navegación
                } else {
                    // Error al crear la cuenta
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
                        is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
                        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo electrónico."
                        else -> "Error al crear la cuenta. Por favor, intenta de nuevo."
                    }
                    onLoginError(errorMessage)
                }
            }
    }

    fun onLoginSuccess() {
        state = state.copy(
            isLoading = false,
            errorMessage = "",
            errorLabel = false
        )
    }

    fun onLoginError(errorMessage: String) {
        state = state.copy(
            isLoading = false,
            errorMessage = errorMessage,
            errorLabel = true
        )
    }

    fun buttonEnabled() {
        if(state.email.isNotEmpty() && state.password.isNotEmpty()){
            state = state.copy(isLoginEnabled = true)
        }
        else{
            state = state.copy(isLoginEnabled = false)
        }
    }
}