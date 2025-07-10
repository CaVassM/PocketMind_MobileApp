package com.example.ta_movil.Components

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.ta_movil.ViewModels.AuthViewModel
import com.example.ta_movil.ViewModels.ForgotViewModel
import com.example.ta_movil.ViewModels.RegisterViewModel
import com.example.ta_movil.Views.userLogin.Home
import com.example.ta_movil.Views.userLogin.Login
import com.example.ta_movil.Views.userLogin.createAccount.CreateAccount
import com.example.ta_movil.Views.userLogin.createAccount.CreateSuccess
import com.example.ta_movil.Views.userLogin.createAccount.VerifyIdentity
import com.example.ta_movil.Views.userLogin.forgotPassword.ForgotPassword
import com.example.ta_movil.Views.userLogin.forgotPassword.PasswordSuccess
import com.google.firebase.auth.FirebaseAuth

// Implementación de las rutas de autenticación
fun NavGraphBuilder.implementAuthRoutes(
    navController: NavHostController,
    auth: FirebaseAuth,
    loginViewModel: AuthViewModel,
    registerViewModel: RegisterViewModel,
    forgotViewModel: ForgotViewModel
) {
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
            onForgotPassword = { navController.navigate("ForgotPassword") },
            auth = auth,
            loginViewModel = loginViewModel
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
            onPrev = { navController.navigate("Login") },
            auth = auth,
            registerViewModel = registerViewModel
        )
    }

    composable("VerifyIdentity"){
        VerifyIdentity(
            onSuccess = { navController.navigate("CreateSuccess") },
            auth = auth,
            registerViewModel = registerViewModel
        )
    }

    composable("CreateSuccess"){
        CreateSuccess(
            onNext = { navController.navigate("Login") }
        )
    }

    /////////////// Flujo recuperar contraseña ///////////////////
    composable("ForgotPassword"){
        ForgotPassword(
            navController = navController,
            onSuccess = { navController.navigate("PasswordSuccess") },
            forgotViewModel = forgotViewModel,
            auth = auth
        )
    }

    composable("PasswordSuccess"){
        PasswordSuccess(
            onSucess = { navController.navigate("Login") }
        )
    }
}