package com.example.ta_movil.ViewModels.userLogin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ta_movil.Model.pagesInit.forgotState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotViewModel : ViewModel(){
    var state by mutableStateOf(forgotState())
        private set
    // Procedemos con implementar los métodos

    // Pantalla de forgot_password
    fun onCorreoInput(correo: String){
        state = state.copy(correo = correo)
        buttonVerifyEnabled()
    }

    fun buttonVerifyEnabled() {
        if(state.correo.isNotEmpty()){
            state = state.copy(buttonEnabled = true)
        }
    }

    fun onForgotPassword(auth: FirebaseAuth, onSuccess: () -> Unit){
        // Estamos dando al botón de enviar.
        // POr tanto, se reseteará el correo
        state = state.copy(isLoading = true)
        auth.sendPasswordResetEmail(state.correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Correo enviado con éxito
                    onSuccess()
                } else {
                    // Error al enviar el correo
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
                        else -> "Ocurrió un error. Intente nuevamente."
                    }
                    onErrorMessage(errorMessage)

                }
            }
        onLoading(false)
    }



    fun onErrorMessage(errorMessage: String){
        state = state.copy(errorMessage = errorMessage)
        onErrorLabel(true)
    }

    fun onLoading(isLoading: Boolean){
        state = state.copy(isLoading = isLoading)
    }

    fun onErrorLabel(errorLabel: Boolean){
        state = state.copy(errorLabel = errorLabel)
    }










}