package com.example.ta_movil.Components

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.ta_movil.Views.userLogin.Home
import com.example.ta_movil.Views.userLogin.Login
import com.example.ta_movil.Views.userLogin.createAccount.CreateAccount
import com.example.ta_movil.Views.userLogin.createAccount.CreateSuccess
import com.example.ta_movil.Views.userLogin.createAccount.VerifyIdentity
import com.example.ta_movil.Views.userLogin.forgotPassword.EstablishPassword
import com.example.ta_movil.Views.userLogin.forgotPassword.ForgotPassword
import com.example.ta_movil.Views.userLogin.forgotPassword.PasswordSuccess
import com.example.ta_movil.Views.userLogin.forgotPassword.VerifyEmail

fun NavGraphBuilder.authNavigation(navController: NavHostController) {
    // Declaramos los flows a utilizar.
    composable("Home"){
        Home(
            onNext = { navController.navigate("Login") }
        )
    }

    composable("Login"){
        // Dashboard en standby, se tiene que enlazar
        Login(
            onSuccess = { navController.navigate("Dashboard") },
            onCreateAccount = { navController.navigate("CreateAccount") },
            onForgotPassword = { navController.navigate("ForgotPassword") }
        )
    }

    // Pendiente. Este se encargara de mandar a otro Flow.
    composable("Dashboard"){
        // Dashboard()
    }

    /////////////// Flujo crear cuenta ///////////////////
    composable("CreateAccount"){
        CreateAccount(
            onNext = { navController.navigate("VerifyIdentity") },
            onPrev = { navController.navigate("Login") }
        )
    }

    composable("VerifyIdentity"){
        VerifyIdentity(
            onSuccess = { navController.navigate("CreateSuccess") }
        )
    }

    composable("CreateSuccess"){
        CreateSuccess(
            onNext = { navController.navigate("Dashboard")}
        )
    }

    /////////////// Flujo olvide contrasenia ///////////////////

    composable("ForgotPassword"){
        ForgotPassword(
            navController = navController,
            onSuccess = { navController.navigate("VerifyEmail") }
        )
    }

    composable("VerifyEmail"){
        VerifyEmail(
            onSuccess = { navController.navigate("EstablishPassword") }
        )
    }

    composable("EstablishPassword"){
        EstablishPassword(
            onSuccess = { navController.navigate("PasswordSuccess") }
        )
    }

    composable("PasswordSuccess") {
        PasswordSuccess(
            onSucess = { navController.navigate("Login") }
        )
    }


}