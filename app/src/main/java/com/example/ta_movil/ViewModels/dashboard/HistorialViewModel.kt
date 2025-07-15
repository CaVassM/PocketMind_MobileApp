package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Models.HistorialTransaction
import com.example.ta_movil.Models.TransactionGroup
import com.example.ta_movil.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistorialViewModel(
    private val dashboardViewModel: DashboardViewModel
) : ViewModel() {

    // Estado para controlar la visibilidad del modal de nueva transacción
    var showTransactionModal by mutableStateOf(false)
        private set

    // Estado para controlar la visibilidad del modal de edición
    var showEditTransactionModal by mutableStateOf(false)
        private set

    // Transacción seleccionada para edición
    var selectedTransaction by mutableStateOf<HistorialTransaction?>(null)
        private set

    // Estados para el modal de edición
    var editAmount by mutableStateOf("")
        private set
    var editDescription by mutableStateOf("")
        private set
    var editDate by mutableStateOf("")
        private set
    var editPaymentMethod by mutableStateOf("")
        private set
    var editTransactionType by mutableStateOf(TransactionType.EXPENSE)
        private set

    // Grupos de transacciones para mostrar en la UI
    var transactionGroups by mutableStateOf<List<TransactionGroup>>(emptyList())
        private set

    // Estados derivados del DashboardViewModel
    val isLoading: Boolean get() = dashboardViewModel.isLoading
    val errorMessage: String? get() = dashboardViewModel.errorMessage
    val currentScreen: Screen get() = dashboardViewModel.currentScreen

    // Formateador de fecha
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale("es", "ES"))
    private val monthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))

    init {
        // Observar cambios en las transacciones del DashboardViewModel
        updateTransactionGroups()
    }

    fun updateTransactionGroups() {
        val transactions = dashboardViewModel.transactions
        println("Actualizando grupos con ${transactions.size} transacciones")

        if (transactions.isEmpty()) {
            transactionGroups = emptyList()
            return
        }

        transactionGroups = mapTransactionsToGroups(transactions)
        println("Grupos creados: ${transactionGroups.size}")
    }

    fun loadTransactions() {
        viewModelScope.launch {
            println("Cargando transacciones desde HistorialViewModel")
            dashboardViewModel.loadTransactions()
            // La actualización de grupos se hará automáticamente cuando cambien las transacciones
        }
    }

    fun showAddTransactionModal() {
        showTransactionModal = true
    }

    fun hideTransactionModal() {
        showTransactionModal = false
    }

    fun showEditTransactionModal(transaction: HistorialTransaction) {
        selectedTransaction = transaction

        // Cargar los datos actuales en los campos de edición
        editAmount = kotlin.math.abs(transaction.amount).toString()
        editDescription = transaction.description
        editPaymentMethod = transaction.paymentMethod

        // Buscar la transacción original para obtener la fecha y tipo
        val originalTransaction = dashboardViewModel.transactions.find { it.id == transaction.id }
        editDate = originalTransaction?.date ?: ""
        editTransactionType = originalTransaction?.type ?: TransactionType.EXPENSE

        showEditTransactionModal = true
    }

    fun hideEditTransactionModal() {
        showEditTransactionModal = false
        selectedTransaction = null
        clearEditFields()
    }

    fun updateEditAmount(amount: String) {
        editAmount = amount
    }

    fun updateEditDescription(description: String) {
        editDescription = description
    }

    fun updateEditDate(date: String) {
        editDate = date
    }

    fun duplicateTransaction(transaction: HistorialTransaction) {
        viewModelScope.launch {
            val originalTransaction = dashboardViewModel.transactions.find { it.id == transaction.id }
            originalTransaction?.let { original ->
                val newId = UUID.randomUUID().toString()

                val duplicatedTransaction = Transaction(
                    id = newId,
                    description = original.description,
                    amount = original.amount,
                    type = original.type,
                    paymentMethod = original.paymentMethod,
                    categoryId = original.categoryId,
                    date = original.date,
                    timestamp = System.currentTimeMillis()
                )

                dashboardViewModel.addTransaction(
                    transaction = duplicatedTransaction,
                    onSuccess = {
                        println("Transacción duplicada correctamente")
                        updateTransactionGroups()
                    },
                    onFailure = { error ->
                        println("Error al duplicar transacción: $error")
                    }
                )
            }
        }
    }

    fun saveEditedTransaction() {
        selectedTransaction?.let { transaction ->
            viewModelScope.launch {
                val amount = editAmount.toDoubleOrNull() ?: 0.0
                val finalAmount = if (editTransactionType == TransactionType.EXPENSE) amount else amount

                val updatedTransaction = Transaction(
                    id = transaction.id,
                    description = editDescription,
                    amount = finalAmount,
                    type = editTransactionType,
                    paymentMethod = editPaymentMethod,
                    date = editDate,
                    categoryId = transaction.categoryId,
                    timestamp = System.currentTimeMillis()
                )

                dashboardViewModel.updateTransaction(updatedTransaction)
                updateTransactionGroups()
                hideEditTransactionModal()
            }
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            dashboardViewModel.deleteTransaction(
                transactionId = transactionId,
                onSuccess = {
                    println("Transacción eliminada correctamente")
                    updateTransactionGroups()
                },
                onFailure = { error ->
                    println("Error al eliminar transacción: $error")
                }
            )
        }
    }

    fun navigateTo(screen: Screen) {
        dashboardViewModel.navigateTo(screen)
    }

    fun retryLoadTransactions() {
        loadTransactions()
    }

    fun clearError() {
        dashboardViewModel.clearError()
    }

    private fun clearEditFields() {
        editAmount = ""
        editDescription = ""
        editDate = ""
        editPaymentMethod = ""
        editTransactionType = TransactionType.EXPENSE
    }

    private fun mapTransactionsToGroups(transactions: List<Transaction>): List<TransactionGroup> {
        return transactions
            .groupBy { it.date }
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
                    transactions = transactionList
                        .map { mapToHistorialTransaction(it) }
                        .sortedByDescending { it.amount }
                )
            }
            .sortedByDescending { group ->
                try {
                    parseDateForSorting(group.transactions.firstOrNull()?.let {
                        dashboardViewModel.transactions.find { tx -> tx.id == it.id }?.date
                    } ?: "")
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            }
    }

    private fun mapToHistorialTransaction(transaction: Transaction): HistorialTransaction {
        return HistorialTransaction(
            id = transaction.id,
            description = transaction.description,
            paymentMethod = transaction.paymentMethod.ifEmpty { "Efectivo" },
            amount = when (transaction.type) {
                TransactionType.INCOME -> transaction.amount
                TransactionType.EXPENSE -> -transaction.amount
            },
            type = transaction.type,
            icon = when (transaction.paymentMethod.lowercase()) {
                "efectivo" -> R.drawable.cash
                "tarjeta" -> R.drawable.card
                "transferencia" -> R.drawable.transfer
                else -> R.drawable.other
            },
            iconColor = when (transaction.type) {
                TransactionType.INCOME -> ColorsTheme.incomeColor
                TransactionType.EXPENSE -> ColorsTheme.expenseColor
            },
            categoryId = transaction.categoryId
        )
    }

    private fun parseDateForSorting(dateString: String): Long {
        return try {
            when {
                dateString.contains("/") -> {
                    val parts = dateString.split("/")
                    if (parts.size >= 3) {
                        val day = parts[0].toInt()
                        val month = parts[1].toInt() - 1 // Calendar es 0-based
                        val year = parts[2].toInt()
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        calendar.timeInMillis
                    } else {
                        System.currentTimeMillis()
                    }
                }
                dateString.contains("-") -> {
                    val parts = dateString.split("-")
                    if (parts.size >= 3) {
                        val year = parts[0].toInt()
                        val month = parts[1].toInt() - 1
                        val day = parts[2].toInt()
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        calendar.timeInMillis
                    } else {
                        System.currentTimeMillis()
                    }
                }
                else -> {
                    dateString.toLongOrNull() ?: System.currentTimeMillis()
                }
            }
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun extractDayFromDate(dateString: String): String {
        return try {
            when {
                dateString.contains("/") -> {
                    val parts = dateString.split("/")
                    if (parts.size >= 1) String.format("%02d", parts[0].toInt()) else "01"
                }
                dateString.contains("-") -> {
                    val parts = dateString.split("-")
                    if (parts.size >= 3) String.format("%02d", parts[2].toInt()) else "01"
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

    private fun extractDayOfWeekFromDate(dateString: String): String {
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