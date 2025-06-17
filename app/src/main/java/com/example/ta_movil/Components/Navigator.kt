package com.example.ta_movil.Components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigator(){
    val navController = rememberNavController()

    // Se declaran los viewModels. Por ahora estos
    // val loginViewModel : LoginViewModel = viewModel()
    // val calculatorViewModel : CalculatorViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "Home"
    ){
        // Aqui se colocaran flujos del aplicativo.
        // Chequen como esta hecho el flow del login.
        authNavigation(navController)

        // Pendiente flujo de


    }

}