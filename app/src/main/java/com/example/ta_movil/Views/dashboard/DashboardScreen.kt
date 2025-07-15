package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.ViewModels.dashboard.SavingGoal
import com.example.ta_movil.ui.theme.AppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.Components.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                currentScreen = viewModel.currentScreen,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard")
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos")
                        Screen.Configuracion -> navController.navigate("configuracion")
                        Screen.Goals -> navController.navigate("goals")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Contenido principal del dashboard
                Text(
                    text = "Bienvenido a PocketMind",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}



@Composable
fun SavingGoalCard(
    goal: SavingGoal,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.primaryBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.primaryText
                    )
                    Text(
                        text = "${goal.currentAmount} / ${goal.targetAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.primaryText
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar"
                        )
                    }
                }
            }

            // Barra de progreso
            LinearProgressIndicator(
                progress = { (goal.currentAmount / goal.targetAmount).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// Extension para obtener el icono de cada pantalla
val Screen.icon: @Composable () -> Unit
    @Composable
    get() = {
        when (this) {
            Screen.Dashboard -> Icon(Icons.Filled.Home, contentDescription = "Dashboard")
            Screen.IngresosEgresos -> Icon(Icons.Filled.Add, contentDescription = "Ingresos y Egresos")
            Screen.Configuracion -> Icon(Icons.Filled.Settings, contentDescription = "Configuración")
            Screen.Goals -> Icon(Icons.Filled.Build, contentDescription = "Metas Personales")
        }
    }
