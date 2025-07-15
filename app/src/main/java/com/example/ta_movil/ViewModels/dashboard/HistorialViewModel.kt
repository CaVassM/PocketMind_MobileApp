package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Models.HistorialTransaction
import com.example.ta_movil.Models.TransactionGroup
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.ViewModels.dashboard.Transaction
import com.example.ta_movil.ViewModels.dashboard.TransactionType
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel específico para la pantalla de historial
 * Maneja el estado de la UI y la lógica de presentación
 */
class HistorialViewModel(
    private val dashboardViewModel: DashboardViewModel
) : ViewModel() {

    // Estado para controlar la visibilidad del modal
    var showTransactionModal by mutableStateOf(false)
        private set

    // Grupos de transacciones para mostrar en la UI
    var transactionGroups by mutableStateOf<List<TransactionGroup>>(emptyList())
        private set

    // Estados derivados del DashboardViewModel
    val isLoading: Boolean get() = dashboardViewModel.isLoading
    val errorMessage: String? get() = dashboardViewModel.errorMessage
    val currentScreen: Screen get() = dashboardViewModel.currentScreen

    init {
        // Observar cambios en las transacciones del DashboardViewModel
        updateTransactionGroups()
    }

    /**
     * Actualiza los grupos de transacciones basándose en los datos del DashboardViewModel
     */
    fun updateTransactionGroups() {
        transactionGroups = mapTransactionsToGroups(dashboardViewModel.transactions)
    }

    /**
     * Carga las transacciones desde el repositorio
     */
    fun loadTransactions() {
        viewModelScope.launch {
            dashboardViewModel.loadTransactions()
        }
    }

    /**
     * Muestra el modal para agregar nueva transacción
     */
    fun showAddTransactionModal() {
        showTransactionModal = true
    }

    /**
     * Oculta el modal de transacción
     */
    fun hideTransactionModal() {
        showTransactionModal = false
    }

    /**
     * Elimina una transacción y actualiza la lista
     */
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            dashboardViewModel.deleteTransaction(transactionId)
            updateTransactionGroups()
        }
    }

    /**
     * Navega a una pantalla específica
     */
    fun navigateTo(screen: Screen) {
        dashboardViewModel.navigateTo(screen)
    }

    /**
     * Reintenta cargar las transacciones en caso de error
     */
    fun retryLoadTransactions() {
        loadTransactions()
    }

    // FUNCIONES PRIVADAS PARA MAPEO Y UTILIDADES

    /**
     * Convierte una lista de transacciones en grupos organizados por fecha
     */
    private fun mapTransactionsToGroups(transactions: List<Transaction>): List<TransactionGroup> {
        return transactions.groupBy { it.date }
            .map { (date, transactionList) ->
                val totalAmount = transactionList.sumOf { transaction ->
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
                    transactions = transactionList.map { mapToHistorialTransaction(it) }
                )
            }
    }

    /**
     * Convierte una transacción del ViewModel a una transacción del historial
     */
    private fun mapToHistorialTransaction(transaction: Transaction): HistorialTransaction {
        return HistorialTransaction(
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

    /**
     * Extrae el día del mes de una fecha en formato string
     */
    private fun extractDayFromDate(dateString: String): String {
        return try {
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
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dateString.toLongOrNull() ?: System.currentTimeMillis()
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
                }
            }
        } catch (e: Exception) {
            "01"
        }
    }

    /**
     * Extrae el día de la semana de una fecha en formato string
     */
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
                    calendar.timeInMillis = dateString.toLongOrNull() ?: System.currentTimeMillis()
                }
            }

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Domingo"
                Calendar.MONDAY -> "Lunes"
                Calendar.TUESDAY -> "Martes"
                Calendar.WEDNESDAY -> "Miércoles"
                Calendar.THURSDAY -> "Jueves"
                Calendar.FRIDAY -> "Viernes"
                Calendar.SATURDAY -> "Sábado"
                else -> "Lunes"
            }
        } catch (e: Exception) {
            "Lunes"
        }
    }

    /**
     * Formatea una fecha para mostrar como "Mes Año"
     */
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
}