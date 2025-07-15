package com.example.ta_movil.Components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ta_movil.ViewModels.userLogin.AuthViewModel
import com.example.ta_movil.ViewModels.userLogin.ForgotViewModel
import com.example.ta_movil.ViewModels.userLogin.RegisterViewModel
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.Views.userLogin.Login
import com.example.ta_movil.Views.userLogin.Home
import com.example.ta_movil.Views.userLogin.createAccount.CreateAccount
import com.example.ta_movil.Views.userLogin.createAccount.CreateSuccess
import com.example.ta_movil.Views.userLogin.createAccount.VerifyIdentity
import com.example.ta_movil.Views.userLogin.forgotPassword.ForgotPassword
import com.example.ta_movil.Views.userLogin.forgotPassword.PasswordSuccess
import com.example.ta_movil.Views.dashboard.DashboardScreen
import com.example.ta_movil.Views.dashboard.GoalsScreen
import com.example.ta_movil.Views.dashboard.ConfiguracionScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialModalViewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialViewModel
import com.example.ta_movil.Views.HistorialScreen
import com.example.ta_movil.ViewModels.dashboard.GoalsModalViewModel

@Composable
fun MainNavigation(
    auth: FirebaseAuth,
    startDestination: String = "login"
) {
    val navController = rememberNavController()
    
    // Inicializar ViewModel
    // ViewModels antes de iniciar a la aplicacion
    val authViewModel: AuthViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val forgotViewModel: ForgotViewModel = viewModel()

    // ViewModels despues de iniciar a la aplicacion
    val dashboardViewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Navegación de autenticación
        authNavigation(navController, auth, authViewModel, registerViewModel, forgotViewModel)
        
        // Navegación principal con BottomNavigationBar
        composable("dashboard") {
            DashboardScreen(navController, dashboardViewModel)
        }

        composable("goals") {
            // 3) El modal (puede seguir siendo un viewModel normal)
            val goalsModalViewModel: GoalsModalViewModel = viewModel()

            // 4) Lanza tu pantalla
            GoalsScreen(
                navController,
                dashboardViewModel,
                dashboardViewModel,
                goalsModalViewModel
            )
        }

        composable("ingresos_egresos") {


            // 2) Crea tu HistorialViewModel pasándole ese dashboardViewModel
            //    Usamos `remember` para que Compose lo retenga mientras el composable viva
            val historialViewModel = remember(dashboardViewModel) {
                HistorialViewModel(dashboardViewModel)
            }

            // 3) El modal (puede seguir siendo un viewModel normal)
            val historialModalViewModel: HistorialModalViewModel = viewModel()

            // 4) Lanza tu pantalla
            HistorialScreen(
                navController,
                dashboardViewModel,
                historialViewModel,
                historialModalViewModel
            )
        }

        // YA NO USAR. ESTE SERA UNA SECCION DE CATEGORIAS O METAS.
        composable("historial") {
            // HistorialScreen(navController, dashboardViewModel)
        }
        
        composable("configuracion") {
            ConfiguracionScreen(navController, auth)
        }
    }
}

fun NavGraphBuilder.authNavigation(
    navController: NavHostController,
    auth: FirebaseAuth,
    authViewModel: AuthViewModel,
    registerViewModel: RegisterViewModel,
    forgotViewModel: ForgotViewModel
) {
    composable("home") {
        Home(
            onNext = { navController.navigate("login") }
        )
    }
    
    composable("login") {
        Login(
            onSuccess = { navController.navigate("dashboard") },
            onCreateAccount = { navController.navigate("create_account") },
            onForgotPassword = { navController.navigate("forgot_password") },
            auth = auth,
            loginViewModel = authViewModel
        )
    }
    
    composable("create_account") {
        CreateAccount(
            onNext = { navController.navigate("verify_identity") },
            onPrev = { navController.popBackStack() },
            auth = auth,
            registerViewModel = registerViewModel
        )
    }
    
    composable("verify_identity") {
        VerifyIdentity(
            onSuccess = { navController.navigate("create_success") },
            auth = auth,
            registerViewModel = registerViewModel
        )
    }
    
    composable("create_success") {
        CreateSuccess(
            onNext = { navController.navigate("dashboard") }
        )
    }
    
    composable("forgot_password") {
        ForgotPassword(
            navController = navController,
            onSuccess = { navController.navigate("password_success") },
            auth = auth,
            forgotViewModel = forgotViewModel
        )
    }
    
    composable("password_success") {
        PasswordSuccess(
            onSucess = { navController.navigate("login") }
        )
    }
}
