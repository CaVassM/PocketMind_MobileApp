package com.example.ta_movil.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Components.BottomNavigationBar
import com.example.ta_movil.Models.HistorialTransaction
import com.example.ta_movil.Models.TransactionGroup
import com.example.ta_movil.ViewModels.dashboard.AuthSharedViewModel
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialModalViewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.Views.dashboard.TransactionModal
import com.example.ta_movil.Views.dashboard.EditTransactionModal

/**
 * Pantalla del historial que muestra todas las transacciones agrupadas por fecha
 * y permite agregar nuevas transacciones mediante un modal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel,
    historialViewModel: HistorialViewModel,
    historialModalViewModel: HistorialModalViewModel,
    authSharedViewModel: AuthSharedViewModel
) {
    // Cargar transacciones al inicio
    LaunchedEffect(Unit) {
        historialViewModel.loadTransactions()
    }

    // Actualizar grupos cuando cambien las transacciones
    LaunchedEffect(dashboardViewModel.transactions) {
        historialViewModel.updateTransactionGroups()
    }

    Scaffold(
        containerColor = ColorsTheme.backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Acción del menú */ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción de compartir */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { authSharedViewModel.showLogoutDialog() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
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
                onClick = { historialViewModel.showAddTransactionModal() },
                containerColor = ColorsTheme.fabColor,
                contentColor = ColorsTheme.headerColor,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar transacción"
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = historialViewModel.currentScreen,
                onNavigate = { screen ->
                    historialViewModel.navigateTo(screen)
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard")
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos")
                        Screen.Categorias -> navController.navigate("categorias")
                        Screen.Configuracion -> navController.navigate("configuracion")
                        Screen.Goals -> navController.navigate("goals")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        HistorialContent(
            paddingValues = paddingValues,
            historialViewModel = historialViewModel
        )

        // Diálogo de logout
        val showLogoutDialog by authSharedViewModel.showLogoutDialog.collectAsState()
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { authSharedViewModel.hideLogoutDialog() },
                title = { Text("Cerrar sesión") },
                text = { Text("¿Estás seguro que deseas cerrar sesión?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            authSharedViewModel.logout(navController)
                        }
                    ) {
                        Text("Confirmar", color = ColorsTheme.primaryText)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            authSharedViewModel.hideLogoutDialog()
                        }
                    ) {
                        Text("Cancelar", color = ColorsTheme.secondaryText)
                    }
                }
            )
        }

        // Modal para agregar nueva transacción
        TransactionModal(
            isVisible = historialViewModel.showTransactionModal,
            onDismiss = { historialViewModel.hideTransactionModal() },
            viewModel = historialModalViewModel,
            dashboardViewModel = dashboardViewModel
        )

        // Modal para editar transacción
        EditTransactionModal(
            isVisible = historialViewModel.showEditTransactionModal,
            onDismiss = { historialViewModel.hideEditTransactionModal() },
            transaction = historialViewModel.selectedTransaction,
            amount = historialViewModel.editAmount,
            description = historialViewModel.editDescription,
            date = historialViewModel.editDate,
            paymentMethod = historialViewModel.editPaymentMethod,
            transactionType = historialViewModel.editTransactionType,
            onAmountChange = { historialViewModel.updateEditAmount(it) },
            onDescriptionChange = { historialViewModel.updateEditDescription(it) },
            onDateChange = { historialViewModel.updateEditDate(it) },
            onSave = { historialViewModel.saveEditedTransaction() }
        )
    }

}

/**
 * Contenido principal de la pantalla de historial
 */
@Composable
private fun HistorialContent(
    paddingValues: PaddingValues,
    historialViewModel: HistorialViewModel
) {
    when {
        historialViewModel.isLoading -> {
            LoadingState(paddingValues)
        }
        historialViewModel.errorMessage != null -> {
            ErrorState(
                paddingValues = paddingValues,
                errorMessage = historialViewModel.errorMessage ?: "Error desconocido",
                onRetry = { historialViewModel.retryLoadTransactions() },
                onClearError = { historialViewModel.clearError() }
            )
        }
        historialViewModel.transactionGroups.isEmpty() -> {
            EmptyState(
                paddingValues = paddingValues,
                onAddTransaction = { historialViewModel.showAddTransactionModal() }
            )
        }
        else -> {
            TransactionsList(
                paddingValues = paddingValues,
                transactionGroups = historialViewModel.transactionGroups,
                onTransactionClick = { transaction ->
                    historialViewModel.showEditTransactionModal(transaction)
                },
                onDeleteTransaction = { transactionId ->
                    historialViewModel.deleteTransaction(transactionId)
                },
                onDuplicateTransaction = { transaction ->
                    historialViewModel.duplicateTransaction(transaction)
                }
            )
        }
    }
}

/**
 * Estado de carga
 */
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

/**
 * Estado de error
 */
@Composable
private fun ErrorState(
    paddingValues: PaddingValues,
    errorMessage: String,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = errorMessage,
                color = ColorsTheme.expenseColor,
                fontSize = 16.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorsTheme.headerColor
                    )
                ) {
                    Text("Reintentar")
                }

                OutlinedButton(
                    onClick = onClearError,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ColorsTheme.headerColor
                    )
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

/**
 * Estado vacío
 */
@Composable
private fun EmptyState(
    paddingValues: PaddingValues,
    onAddTransaction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No hay transacciones registradas",
                color = ColorsTheme.secondaryText,
                fontSize = 16.sp
            )

            Button(
                onClick = onAddTransaction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorsTheme.headerColor
                )
            ) {
                Text("Agregar primera transacción")
            }
        }
    }
}

/**
 * Lista de transacciones
 */
@Composable
private fun TransactionsList(
    paddingValues: PaddingValues,
    transactionGroups: List<TransactionGroup>,
    onTransactionClick: (HistorialTransaction) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onDuplicateTransaction: (HistorialTransaction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(transactionGroups) { group ->
            TransactionGroupCard(
                group = group,
                onTransactionClick = onTransactionClick,
                onDeleteTransaction = onDeleteTransaction,
                onDuplicateTransaction = onDuplicateTransaction
            )
        }
    }
}

/**
 * Tarjeta que muestra un grupo de transacciones por fecha
 */
@Composable
private fun TransactionGroupCard(
    group: TransactionGroup,
    onTransactionClick: (HistorialTransaction) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onDuplicateTransaction: (HistorialTransaction) -> Unit
) {
    Column {
        // Encabezado del grupo
        TransactionGroupHeader(
            dayOfMonth = group.dayOfMonth,
            dayOfWeek = group.dayOfWeek,
            date = group.date,
            totalAmount = group.totalAmount
        )

        // Lista de transacciones del día
        group.transactions.forEach { transaction ->
            TransactionItem(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) },
                onDelete = { onDeleteTransaction(transaction.id) },
                onDuplicate = { onDuplicateTransaction(transaction) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Encabezado del grupo de transacciones
 */
@Composable
private fun TransactionGroupHeader(
    dayOfMonth: String,
    dayOfWeek: String,
    date: String,
    totalAmount: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número del día
            Text(
                text = dayOfMonth,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsTheme.primaryText
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Día de la semana y mes
            Column {
                Text(
                    text = dayOfWeek,
                    fontSize = 14.sp,
                    color = ColorsTheme.secondaryText
                )
                Text(
                    text = date,
                    fontSize = 14.sp,
                    color = ColorsTheme.secondaryText
                )
            }
        }

        // Total del día
        Text(
            text = if (totalAmount >= 0) "+S/${String.format("%.2f", totalAmount)}"
            else "-S/${String.format("%.2f", kotlin.math.abs(totalAmount))}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (totalAmount >= 0) ColorsTheme.incomeColor else ColorsTheme.expenseColor
        )
    }
}

/**
 * Item individual de transacción
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionItem(
    transaction: HistorialTransaction,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColorsTheme.cardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de la transacción
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(transaction.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = transaction.icon),
                    contentDescription = transaction.description,
                    tint = transaction.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información de la transacción
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorsTheme.primaryText
                )

                Text(
                    text = transaction.paymentMethod,
                    fontSize = 14.sp,
                    color = ColorsTheme.secondaryText
                )
            }

            // Monto
            Text(
                text = if (transaction.amount >= 0) "+S/${String.format("%.2f", transaction.amount)}"
                else "-S/${String.format("%.2f", kotlin.math.abs(transaction.amount))}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.amount >= 0) ColorsTheme.incomeColor else ColorsTheme.expenseColor
            )

            // Menú de opciones
            Box {
                IconButton(
                    onClick = { showOptionsMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = ColorsTheme.secondaryText
                    )
                }

                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { showOptionsMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = {
                            showOptionsMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicar") },
                        onClick = {
                            showOptionsMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Eliminar",
                                color = ColorsTheme.expenseColor
                            )
                        },
                        onClick = {
                            showOptionsMenu = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Transacción") },
            text = {
                Text("¿Estás seguro de que deseas eliminar esta transacción? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ColorsTheme.expenseColor
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}