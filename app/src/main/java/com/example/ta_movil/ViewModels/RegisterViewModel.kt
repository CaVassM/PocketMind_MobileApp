package com.example.ta_movil.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ta_movil.Model.pagesInit.registerState
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterViewModel : ViewModel(){
    var state by mutableStateOf(registerState())
        private set

    fun onNameInput(name: String){
        state = state.copy(name = name)
        buttonEnabled()
    }
    fun onEmailInput(email: String){
        state = state.copy(email = email)
        buttonEnabled()
    }
    fun onPhoneNumberInput(phoneNumber: String){
        state = state.copy(phoneNumber = phoneNumber)
        buttonEnabled()
    }
    fun onPasswordInput(password: String){
        state = state.copy(password = password)
        buttonEnabled()
    }
    fun onConfirmPasswordInput(confirmPassword: String){
        state = state.copy(confirmPassword = confirmPassword)
        buttonEnabled()
    }

    fun onErrorMessage(errorMessage: String){
        state = state.copy(errorMessage = errorMessage)
    }

    fun onLoading(isLoading: Boolean){
        state = state.copy(isLoading = isLoading)
    }

    fun buttonEnabled() {
        if(state.name.isNotEmpty() && state.email.isNotEmpty() && state.phoneNumber.isNotEmpty() && state.password.isNotEmpty() && state.confirmPassword.isNotEmpty()){
            state = state.copy(buttonEnabled = true)
        }
    }

    fun onVerify(onNext: () -> Unit, auth: FirebaseAuth) {
        // Se va a registrar directamente al firebase.
        // Primero verificamos si las contraseñas coinciden antes de mandar la solicitud a firebase
        // Caso 1
        if(state.password != state.confirmPassword){
            onErrorMessage("Las contraseñas no coinciden")
            return
        }

        // Caso 2
        if(state.password.length < 8){
            onErrorMessage("La contraseña debe tener al menos 8 caracteres")
            return
        }

        state = state.copy(isLoading = true)
        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = auth.currentUser
                    user?.sendEmailVerification() // No debería de haber null.
                    onNext()
                } else {
                    // Registro fallido
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo."
                        is FirebaseAuthInvalidCredentialsException -> "Lo ingresado no es válido."
                        else -> "Ocurrió un error. Intente nuevamente."
                    }
                    onErrorMessage(errorMessage)
                    onLoading(false)
                }
            }
    }


    fun onErrorMessageVerify(errorMessage: String){
        state = state.copy(errorMessageVerify = errorMessage)
    }

    fun onVerifyIdentity(onNext: () -> Unit, auth: FirebaseAuth) {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    if (user.isEmailVerified) {
                        onErrorMessageVerify("") // Se limpia el error
                        onNext()
                    } else {
                        onErrorMessageVerify("No se ha verificado el correo. Enviando nuevamente...")
                        user.sendEmailVerification()
                    }
                } else {
                    onErrorMessageVerify("No se pudo verificar el estado del usuario. Inténtalo de nuevo.")
                }
            }
        }
    }

    fun onUpdatePassword(currentPassword: String, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email ?: return onResult(false, "Email no disponible")

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, updateTask.exception?.message)
                            }
                        }
                } else {
                    onResult(false, authTask.exception?.message)
                }
            }
    }

}