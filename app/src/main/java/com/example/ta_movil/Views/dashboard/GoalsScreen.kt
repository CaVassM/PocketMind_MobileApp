package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Components.BottomNavigationBar
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.ViewModels.dashboard.SavingGoal
import com.example.ta_movil.ViewModels.dashboard.GoalsModalViewModel
import com.example.ta_movil.Views.dashboard.GoalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = viewModel(),
    goalsViewModel: DashboardViewModel,
    goalsModalViewModel: GoalsModalViewModel
) {
    // Cargar metas al inicio
    LaunchedEffect(Unit) {
        goalsViewModel.loadSavingGoals()
    }

    Scaffold(
        containerColor = ColorsTheme.backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Metas Personales",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorsTheme.headerColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { goalsViewModel.showAddGoalModal() },
                containerColor = ColorsTheme.fabColor,
                contentColor = ColorsTheme.headerColor,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar meta"
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = goalsViewModel.currentScreen,
                onNavigate = { screen ->
                    goalsViewModel.navigateTo(screen)
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard")
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos")
                        Screen.Configuracion -> navController.navigate("configuracion")
                        Screen.Goals -> navController.navigate("goals")
                        Screen.Categorias -> navController.navigate("categorias")
                    }
                }
            )
        }
    ) { paddingValues ->
        GoalsContent(
            paddingValues = paddingValues,
            viewModel = goalsViewModel
        )
    }

    // Modal para agregar/editar meta
    if (goalsViewModel.showAddGoalModal) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(400.dp)
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .background(ColorsTheme.cardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (goalsViewModel.currentEditingGoal != null) "Editar Meta" else "Nueva Meta",
                        color = ColorsTheme.headerColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    var name by remember { mutableStateOf(goalsViewModel.currentEditingGoal?.name ?: "") }
                    var targetAmount by remember { mutableStateOf(goalsViewModel.currentEditingGoal?.targetAmount?.toString() ?: "") }
                    var nameError by remember { mutableStateOf(false) }
                    var amountError by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            nameError = it.isBlank()
                        },
                        label = { Text("Nombre de la meta") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { 
                            targetAmount = it
                            amountError = it.toDoubleOrNull() == null || it.toDouble() <= 0
                        },
                        label = { Text("Monto objetivo") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = amountError
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { goalsViewModel.hideAddGoalModal() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ColorsTheme.headerColor
                            )
                        ) {
                            Text("Cancelar")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                if (!nameError && !amountError) {
                                    val amount = targetAmount.toDouble()
                                    goalsViewModel.saveGoal(name, amount)
                                    goalsViewModel.hideAddGoalModal()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorsTheme.fabColor,
                                contentColor = ColorsTheme.headerColor
                            ),
                            enabled = !nameError && !amountError
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsContent(
    paddingValues: PaddingValues,
    viewModel: DashboardViewModel
) {
    when {
        viewModel.isLoading -> {
            LoadingState(paddingValues)
        }
        viewModel.errorMessage != null -> {
            ErrorState(
                paddingValues = paddingValues,
                errorMessage = viewModel.errorMessage ?: "Error desconocido",
                onRetry = { viewModel.loadSavingGoals() }
            )
        }
        viewModel.savingGoals.isEmpty() -> {
            EmptyState(
                paddingValues = paddingValues,
                onAddGoal = { viewModel.showAddGoalModal() }
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.savingGoals) { goal ->
                    GoalCard(
                        goal = goal,
                        onEdit = { viewModel.showEditGoalModal(goal) },
                        onDelete = { viewModel.deleteSavingGoal(goal.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = ColorsTheme.headerColor
        )
    }
}

@Composable
private fun ErrorState(
    paddingValues: PaddingValues,
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage,
                color = ColorsTheme.expenseColor,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorsTheme.fabColor,
                    contentColor = ColorsTheme.headerColor
                )
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyState(
    paddingValues: PaddingValues,
    onAddGoal: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "No tienes metas personales",
                color = ColorsTheme.secondaryText,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddGoal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorsTheme.fabColor,
                    contentColor = ColorsTheme.headerColor
                )
            ) {
                Text("Agregar meta")
            }
        }
    }
}
