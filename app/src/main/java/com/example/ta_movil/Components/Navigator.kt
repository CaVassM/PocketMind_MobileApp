package com.example.ta_movil.Components

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ta_movil.ViewModels.userLogin.AuthViewModel
import com.example.ta_movil.ViewModels.userLogin.ForgotViewModel
import com.example.ta_movil.ViewModels.userLogin.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigator(auth: FirebaseAuth) {
    val navController = rememberNavController()

    // Se declaran los viewModels. Por ahora estos
    val authViewModel : AuthViewModel = viewModel()
    val registerViewModel : RegisterViewModel = viewModel()
    val forgotViewModel : ForgotViewModel = viewModel()
    // Por ahora el auth acá, normalmente se debería de hacer en la capa de datos (repository)

    NavHost(
        navController = navController,
        startDestination = "Home"
    ){
        // Aqui se colocaran flujos del aplicativo.
        // Chequen como esta hecho el flow del login.
        authNavigation(navController, auth, authViewModel, registerViewModel, forgotViewModel)

        // Pendiente flujo de dashboard
    }
}