package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.ViewModels.DashboardViewModel
import com.example.ta_movil.ui.theme.AppTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import com.example.ta_movil.Components.TransactionCard
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreenAlternativo(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    var selectedMonth by remember { mutableStateOf<LocalDate?>(null) }
    var selectedYear by remember { mutableStateOf<LocalDate?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showFilterDialog = true },
                containerColor = AppTheme.primaryColor
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Filtrar"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Lista de transacciones
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(viewModel.transactions) { transaction ->
                    TransactionCard(transaction)
                }
            }
        }

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filtrar por fecha") },
                text = {
                    Column {
                        // Aquí iría el contenido del diálogo de filtro
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showFilterDialog = false
                            // Aplicar filtros
                        }
                    ) {
                        Text("Aplicar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showFilterDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
