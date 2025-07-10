package com.example.ta_movil.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.ViewModels.DashboardViewModel
import com.example.ta_movil.ViewModels.Transaction
import com.example.ta_movil.ViewModels.TransactionType
import com.example.ta_movil.Components.BottomNavigationBar

import com.example.ta_movil.ViewModels.Screen
import com.example.ta_movil.Views.dashboard.TransactionModal
import java.text.SimpleDateFormat
import java.util.*

// Funciones auxiliares para extraer información de fecha
private fun extractDayFromDate(dateString: String): String {
    return try {
        // Asumiendo formato "dd/MM/yyyy" o "yyyy-MM-dd"
        when {
            dateString.contains("/") -> {
                val parts = dateString.split("/")
                if (parts.size >= 1) parts[0] else "01"
            }
            dateString.contains("-") -> {
                val parts = dateString.split("-")
                if (parts.size >= 3) parts[2] else "01"
            }
            else -> {
                // Si es timestamp o formato diferente, usar fecha actual
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateString.toLongOrNull() ?: System.currentTimeMillis()
                String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
            }
        }
    } catch (e: Exception) {
        "01"
    }
}

private fun extractDayOfWeekFromDate(dateString: String): String {
    return try {
        val calendar = Calendar.getInstance()
        when {
            dateString.contains("/") -> {
                val parts = dateString.split("/")
                if (parts.size >= 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Calendar.MONTH es 0-based
                    val year = parts[2].toInt()
                    calendar.set(year, month, day)
                }
            }
            dateString.contains("-") -> {
                val parts = dateString.split("-")
                if (parts.size >= 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt() - 1
                    val day = parts[2].toInt()
                    calendar.set(year, month, day)
                }
            }
            else -> {
                // Si es timestamp
                calendar.timeInMillis = dateString.toLongOrNull() ?: System.currentTimeMillis()
            }
        }

        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Lunes"
        }
        dayOfWeek
    } catch (e: Exception) {
        "Lunes"
    }
}

private fun formatDateForDisplay(dateString: String): String {
    return try {
        val calendar = Calendar.getInstance()
        when {
            dateString.contains("/") -> {
                val parts = dateString.split("/")
                if (parts.size >= 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1
                    val year = parts[2].toInt()
                    calendar.set(year, month, day)
                }
            }
            dateString.contains("-") -> {
                val parts = dateString.split("-")
                if (parts.size >= 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt() - 1
                    val day = parts[2].toInt()
                    calendar.set(year, month, day)
                }
            }
            else -> {
                calendar.timeInMillis = dateString.toLongOrNull() ?: System.currentTimeMillis()
            }
        }

        val monthName = when (calendar.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "Enero"
            Calendar.FEBRUARY -> "Febrero"
            Calendar.MARCH -> "Marzo"
            Calendar.APRIL -> "Abril"
            Calendar.MAY -> "Mayo"
            Calendar.JUNE -> "Junio"
            Calendar.JULY -> "Julio"
            Calendar.AUGUST -> "Agosto"
            Calendar.SEPTEMBER -> "Septiembre"
            Calendar.OCTOBER -> "Octubre"
            Calendar.NOVEMBER -> "Noviembre"
            Calendar.DECEMBER -> "Diciembre"
            else -> "Enero"
        }

        "$monthName ${calendar.get(Calendar.YEAR)}"
    } catch (e: Exception) {
        "Enero 2025"
    }
}

// Datos de ejemplo para el historial
data class TransactionGroup(
    val date: String,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val totalAmount: Double,
    val transactions: List<HistorialTransaction>
)

data class HistorialTransaction(
    val id: String,
    val description: String,
    val category: String,
    val amount: Double,
    val type: TransactionType,
    val icon: ImageVector,
    val iconColor: Color
)

/**
 * Pantalla del historial que muestra todas las transacciones agrupadas por fecha
 * y permite agregar nuevas transacciones mediante un modal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    // Estado para controlar la visibilidad del modal
    var showTransactionModal by remember { mutableStateOf(false) }

    // Cargar transacciones al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadTransactions()
    }

    // Observar cambios en el estado de error del ViewModel
    // para mostrar mensajes de confirmación o error
    val errorMessage = viewModel.errorMessage
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            // Nota: Necesitarías agregar una función clearError() en el ViewModel
        }
    }

    // Convertir transacciones del ViewModel a grupos por fecha
    val transactionGroups = remember(viewModel.transactions) {
        viewModel.transactions.groupBy { it.date }
            .map { (date, transactions) ->
                val totalAmount = transactions.sumOf { transaction ->
                    when (transaction.type) {
                        TransactionType.INCOME -> transaction.amount
                        TransactionType.EXPENSE -> -transaction.amount
                    }
                }

                TransactionGroup(
                    date = formatDateForDisplay(date),
                    dayOfMonth = extractDayFromDate(date),
                    dayOfWeek = extractDayOfWeekFromDate(date),
                    totalAmount = totalAmount,
                    transactions = transactions.map { transaction ->
                        HistorialTransaction(
                            id = transaction.id,
                            description = transaction.description,
                            category = transaction.paymentMethod.ifEmpty { "Efectivo" },
                            amount = when (transaction.type) {
                                TransactionType.INCOME -> transaction.amount
                                TransactionType.EXPENSE -> -transaction.amount
                            },
                            type = transaction.type,
                            icon = when (transaction.type) {
                                TransactionType.INCOME -> Icons.Default.Add
                                TransactionType.EXPENSE -> Icons.Default.ShoppingCart
                            },
                            iconColor = when (transaction.type) {
                                TransactionType.INCOME -> ColorsTheme.incomeColor
                                TransactionType.EXPENSE -> ColorsTheme.expenseColor
                            }
                        )
                    }
                )
            }
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
                            Icons.Default.Share, // Esto en realidad debe de ser un boton de logout.
                            contentDescription = "Compartir",
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
                onClick = {
                    // Mostrar el modal para agregar nueva transacción
                    showTransactionModal = true
                },
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
                currentScreen = viewModel.currentScreen,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard")
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos")
                        Screen.Historial -> navController.navigate("historial")
                        Screen.Configuracion -> navController.navigate("configuracion")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        if (viewModel.isLoading) {
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
        } else if (viewModel.errorMessage != null) {
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
                        text = viewModel.errorMessage ?: "Error desconocido",
                        color = ColorsTheme.expenseColor,
                        fontSize = 16.sp
                    )

                    Button(
                        onClick = { viewModel.loadTransactions() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorsTheme.headerColor
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        } else if (transactionGroups.isEmpty()) {
            // Estado vacío cuando no hay transacciones
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
                        onClick = { showTransactionModal = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorsTheme.headerColor
                        )
                    ) {
                        Text("Agregar primera transacción")
                    }
                }
            }
        } else {
            // Lista de transacciones agrupadas
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
                        onTransactionClick = { transaction ->
                            // Aquí podrías implementar la funcionalidad para
                            // editar o ver detalles de una transacción
                            // Por ejemplo, mostrar otro modal o navegar a una pantalla de detalles
                        },
                        onDeleteTransaction = { transactionId ->
                            // Eliminar transacción
                            viewModel.deleteTransaction(transactionId)
                        }
                    )
                }
            }
        }
    }

    // Modal para agregar nueva transacción
    TransactionModal(
        isVisible = showTransactionModal,
        onDismiss = {
            showTransactionModal = false
        },
        viewModel = viewModel
    )
}

/**
 * Tarjeta que muestra un grupo de transacciones por fecha
 */
@Composable
fun TransactionGroupCard(
    group: TransactionGroup,
    onTransactionClick: (HistorialTransaction) -> Unit = {},
    onDeleteTransaction: (String) -> Unit = {}
) {
    Column {
        // Fecha y total del día
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
                    text = group.dayOfMonth,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorsTheme.primaryText
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Día de la semana y mes
                Column {
                    Text(
                        text = group.dayOfWeek,
                        fontSize = 14.sp,
                        color = ColorsTheme.secondaryText
                    )
                    Text(
                        text = group.date,
                        fontSize = 14.sp,
                        color = ColorsTheme.secondaryText
                    )
                }
            }

            // Total del día
            Text(
                text = if (group.totalAmount >= 0) "+S/${String.format("%.2f", group.totalAmount)}"
                else "-S/${String.format("%.2f", kotlin.math.abs(group.totalAmount))}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (group.totalAmount >= 0) ColorsTheme.incomeColor else ColorsTheme.expenseColor
            )
        }

        // Lista de transacciones del día
        group.transactions.forEach { transaction ->
            TransactionItem(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) },
                onDelete = { onDeleteTransaction(transaction.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Item individual de transacción con opciones de interacción
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: HistorialTransaction,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
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
                    imageVector = transaction.icon,
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.category,
                        fontSize = 14.sp,
                        color = ColorsTheme.secondaryText
                    )
                }
            }

            // Monto y opciones
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (transaction.amount >= 0) "+S/${String.format("%.2f", transaction.amount)}"
                    else "-S/${String.format("%.2f", kotlin.math.abs(transaction.amount))}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amount >= 0) ColorsTheme.incomeColor else ColorsTheme.expenseColor
                )

                // Botón para eliminar (opcional)
                TextButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ColorsTheme.expenseColor.copy(alpha = 0.7f)
                    )
                ) {
                    Text("Eliminar", fontSize = 12.sp)
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