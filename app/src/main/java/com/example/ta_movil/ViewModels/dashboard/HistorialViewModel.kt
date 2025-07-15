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
import java.util.*

/**
 * ViewModel específico para la pantalla de historial
 * Maneja el estado de la UI y la lógica de presentación
 */
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
            updateTransactionGroups()
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
     * Muestra el modal de edición con los datos de la transacción seleccionada
     */
    fun showEditTransactionModal(transaction: HistorialTransaction) {
        selectedTransaction = transaction

        // Cargar los datos actuales en los campos de edición
        editAmount = kotlin.math.abs(transaction.amount).toString()
        editDescription = transaction.description
        editPaymentMethod = transaction.category

        // Buscar la transacción original para obtener la fecha y tipo
        val originalTransaction = dashboardViewModel.transactions.find { it.id == transaction.id }
        editDate = originalTransaction?.date ?: ""
        editTransactionType = originalTransaction?.type ?: TransactionType.EXPENSE

        showEditTransactionModal = true
    }

    /**
     * Oculta el modal de edición
     */
    fun hideEditTransactionModal() {
        showEditTransactionModal = false
        selectedTransaction = null
        clearEditFields()
    }

    /**
     * Actualiza el campo de cantidad en el modal de edición
     */
    fun updateEditAmount(amount: String) {
        editAmount = amount
    }

    /**
     * Actualiza el campo de descripción en el modal de edición
     */
    fun updateEditDescription(description: String) {
        editDescription = description
    }

    /**
     * Actualiza el campo de fecha en el modal de edición
     */
    fun updateEditDate(date: String) {
        editDate = date
    }

    /**
     * Duplica la transacción seleccionada
     */
    fun duplicateTransaction(transaction: HistorialTransaction) {
        viewModelScope.launch {
            val originalTransaction = dashboardViewModel.transactions.find { it.id == transaction.id }
            originalTransaction?.let { original ->
                // Generar un nuevo ID único para la transacción duplicada
                val newId = UUID.randomUUID().toString()

                val duplicatedTransaction = Transaction(
                    id = newId,
                    description = original.description,
                    amount = original.amount,
                    type = original.type,
                    paymentMethod = original.paymentMethod,
                    date = original.date
                )

                dashboardViewModel.addTransaction(
                    transaction = duplicatedTransaction,
                    onSuccess = {
                        updateTransactionGroups()
                    },
                    onFailure = { error ->
                        // El error ya se maneja en el DashboardViewModel
                    }
                )
            }
        }
    }

    /**
     * Guarda los cambios de la transacción editada
     */
    fun saveEditedTransaction() {
        selectedTransaction?.let { transaction ->
            viewModelScope.launch {
                val amount = editAmount.toDoubleOrNull() ?: 0.0

                // Crear la transacción actualizada
                val updatedTransaction = Transaction(
                    id = transaction.id,
                    description = editDescription,
                    amount = amount,
                    type = editTransactionType,
                    paymentMethod = editPaymentMethod,
                    date = editDate
                )

                // Llamar al método de actualización del DashboardViewModel
                dashboardViewModel.updateTransaction(updatedTransaction)

                // Actualizar grupos después de la edición
                updateTransactionGroups()

                // Cerrar el modal
                hideEditTransactionModal()
            }
        }
    }

    /**
     * Elimina una transacción y actualiza la lista
     */
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            dashboardViewModel.deleteTransaction(
                transactionId = transactionId,
                onSuccess = {
                    updateTransactionGroups()
                },
                onFailure = { error ->
                    // El error ya se maneja en el DashboardViewModel
                }
            )
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

    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        dashboardViewModel.clearError()
    }

    /**
     * Limpia los campos de edición
     */
    private fun clearEditFields() {
        editAmount = ""
        editDescription = ""
        editDate = ""
        editPaymentMethod = ""
        editTransactionType = TransactionType.EXPENSE
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
            .sortedByDescending { group ->
                // Ordenar grupos por fecha más reciente primero
                try {
                    parseDateForSorting(group.transactions.firstOrNull()?.let {
                        dashboardViewModel.transactions.find { tx -> tx.id == it.id }?.date
                    } ?: "")
                } catch (e: Exception) {
                    0L
                }
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
            icon = when (transaction.paymentMethod) {
                "Efectivo" -> R.drawable.cash
                "Tarjeta" -> R.drawable.card
                "Transferencia" -> R.drawable.transfer
                else -> R.drawable.other
            },
            iconColor = when (transaction.type) {
                TransactionType.INCOME -> ColorsTheme.incomeColor
                TransactionType.EXPENSE -> ColorsTheme.expenseColor
            }
        )
    }

    /**
     * Convierte una fecha string a timestamp para ordenamiento
     */
    private fun parseDateForSorting(dateString: String): Long {
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
            calendar.timeInMillis
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * Extrae el día del mes de una fecha en formato string
     */
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