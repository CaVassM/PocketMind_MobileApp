package com.example.ta_movil.Components

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ta_movil.ViewModels.AuthViewModel
import com.example.ta_movil.ViewModels.ForgotViewModel
import com.example.ta_movil.ViewModels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigator(auth: FirebaseAuth) {
    val navController = rememberNavController()

    // Se declaran los viewModels. Por ahora estos
    val loginViewModel : AuthViewModel = viewModel()
    val registerViewModel : RegisterViewModel = viewModel()
    val forgotViewModel : ForgotViewModel = viewModel()
    // Por ahora el auth acá, normalmente se debería de hacer en la capa de datos (repository)

    NavHost(
        navController = navController,
        startDestination = "Home"
    ){
        // Aqui se colocaran flujos del aplicativo.
        // Chequen como esta hecho el flow del login.
        authNavigation(navController, auth, loginViewModel, registerViewModel, forgotViewModel)

        // Pendiente flujo de dashboard


    }

}