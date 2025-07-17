package com.example.ta_movil.Components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ta_movil.ViewModels.userLogin.AuthViewModel
import com.example.ta_movil.ViewModels.userLogin.ForgotViewModel
import com.example.ta_movil.ViewModels.userLogin.RegisterViewModel
import com.example.ta_movil.ViewModels.dashboard.AuthSharedViewModel
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.Views.userLogin.Login
import com.example.ta_movil.Views.userLogin.Home
import com.example.ta_movil.Views.userLogin.createAccount.CreateAccount
import com.example.ta_movil.Views.userLogin.createAccount.CreateSuccess
import com.example.ta_movil.Views.userLogin.createAccount.VerifyIdentity
import com.example.ta_movil.Views.userLogin.forgotPassword.ForgotPassword
import com.example.ta_movil.Views.userLogin.forgotPassword.PasswordSuccess
import com.example.ta_movil.Views.dashboard.DashboardScreen
import com.example.ta_movil.Views.dashboard.GoalsScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.ViewModels.dashboard.CategoriasViewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialModalViewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialViewModel
import com.example.ta_movil.Views.HistorialScreen
import com.example.ta_movil.ViewModels.dashboard.GoalsModalViewModel
import com.example.ta_movil.ViewModels.dashboard.ProfileViewModel
import com.example.ta_movil.Views.dashboard.CategoriasScreen
import com.example.ta_movil.Views.dashboard.ConfigurationScreen

@Composable
fun MainNavigation(
    auth: FirebaseAuth,
    startDestination: String = "login"
) {
    val navController = rememberNavController()
    
    // Inicializar ViewModel
    // ViewModels antes de iniciar a la aplicación
    val authViewModel: AuthViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val forgotViewModel: ForgotViewModel = viewModel()
    val authSharedViewModel: AuthSharedViewModel = viewModel()

    // ViewModels después de iniciar a la aplicación
    val dashboardViewModel: DashboardViewModel = viewModel()
    val categoriasViewModel: CategoriasViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Navegación de autenticación
        authNavigation(navController, auth, authViewModel, registerViewModel, forgotViewModel)
        
        // Navegación principal con BottomNavigationBar
        composable("dashboard") {
            DashboardScreen(
                auth = auth,
                navController = navController,
                dashboardViewModel = dashboardViewModel,
                categoriasViewModel = categoriasViewModel,
                authSharedViewModel = authSharedViewModel
            )
        }

        composable("goals") {
            // 3) El modal (puede seguir siendo un viewModel normal)
            val goalsModalViewModel: GoalsModalViewModel = viewModel()

            // 4) Lanza tu pantalla
            dashboardViewModel.navigateTo(Screen.Goals)
            GoalsScreen(
                navController = navController,
                dashboardViewModel = dashboardViewModel,
                goalsModalViewModel = goalsModalViewModel,
                authSharedViewModel = authSharedViewModel
            )
        }

        composable("ingresos_egresos") {
            // 2) Crea tu HistorialViewModel pasándole ese dashboardViewModel
            val historialViewModel = remember(dashboardViewModel) {
                HistorialViewModel(dashboardViewModel)
            }

            // 3) El modal (puede seguir siendo un viewModel normal)
            val historialModalViewModel: HistorialModalViewModel = viewModel()

            // 4) Lanza tu pantalla
            dashboardViewModel.navigateTo(Screen.IngresosEgresos)
            HistorialScreen(
                navController = navController,
                dashboardViewModel = dashboardViewModel,
                historialViewModel = historialViewModel,
                historialModalViewModel = historialModalViewModel,
                authSharedViewModel = authSharedViewModel
            )
        }

        composable("configuracion") {
            val profileViewModel: ProfileViewModel = viewModel {
                ProfileViewModel(
                    auth = auth,
                    dashboardViewModel = dashboardViewModel
                )
            }
            dashboardViewModel.navigateTo(Screen.Configuracion)
            ConfigurationScreen(
                navController = navController,
                viewModel = profileViewModel,
                authSharedViewModel = authSharedViewModel
            )
        }

        composable("categorias") {
            dashboardViewModel.navigateTo(Screen.Categorias)
            CategoriasScreen(
                viewModel = categoriasViewModel,
                navController = navController,
                authSharedViewModel = authSharedViewModel
            )
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
