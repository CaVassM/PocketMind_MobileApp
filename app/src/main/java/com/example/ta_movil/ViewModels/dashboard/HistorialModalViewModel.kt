package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ta_movil.Model.dashboard.transactionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HistorialModalViewModel : ViewModel() {

    // Estados de la transacción
    private var _transactionState by mutableStateOf(transactionState())
    val transactionState: transactionState get() = _transactionState

    // Estados de validación
    private var _amountError by mutableStateOf("")
    val amountError: String get() = _amountError

    private var _descriptionError by mutableStateOf("")
    val descriptionError: String get() = _descriptionError

    private var _isSubmitting by mutableStateOf(false)
    val isSubmitting: Boolean get() = _isSubmitting

    // Funciones para actualizar el estado de la transacción
    fun updateTransactionType(type: TransactionType) {
        _transactionState = _transactionState.copy(type = type)
    }

    fun updateAmount(amount: String) {
        _transactionState = _transactionState.copy(amount = amount)
        _amountError = "" // Limpiar error al escribir
    }

    fun updateDescription(description: String) {
        _transactionState = _transactionState.copy(description = description)
        _descriptionError = "" // Limpiar error al escribir
    }

    fun updatePaymentMethod(method: String) {
        _transactionState = _transactionState.copy(paymentMethod = method)
    }

    fun updateSelectedDate(date: Date) {
        _transactionState = _transactionState.copy(selectedDate = date)
    }

    // Función de validación
    fun validateForm(): Boolean {
        var isValid = true

        // Validar monto
        if (_transactionState.amount.isBlank()) {
            _amountError = "El monto es requerido"
            isValid = false
        } else {
            try {
                val amountValue = _transactionState.amount.toDouble()
                if (amountValue <= 0) {
                    _amountError = "El monto debe ser mayor a 0"
                    isValid = false
                } else {
                    _amountError = ""
                }
            } catch (e: NumberFormatException) {
                _amountError = "Ingrese un monto válido"
                isValid = false
            }
        }

        // Validar descripción
        if (_transactionState.description.isBlank()) {
            _descriptionError = "La descripción es requerida"
            isValid = false
        } else if (_transactionState.description.length < 3) {
            _descriptionError = "La descripción debe tener al menos 3 caracteres"
            isValid = false
        } else {
            _descriptionError = ""
        }

        return isValid
    }

    // Función para formatear la fecha
    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    // CORREGIDO: Función para guardar la transacción recibiendo DashboardViewModel
    fun saveTransaction(
        dashboardViewModel: DashboardViewModel,
        onSuccess: () -> Unit
    ) {
        // Validar antes de proceder
        if (!validateForm()) {
            return
        }

        _isSubmitting = true

        try {
            // Crear el objeto Transaction con un ID único
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                type = _transactionState.type,
                amount = _transactionState.amount.toDouble(),
                description = _transactionState.description.trim(),
                date = formatDate(_transactionState.selectedDate),
                paymentMethod = _transactionState.paymentMethod
            )

            // Agregar callback para manejar éxito/error
            dashboardViewModel.addTransaction(
                transaction = transaction,
                onSuccess = {
                    // Resetear el formulario
                    resetForm()
                    _isSubmitting = false
                    // Ejecutar callback de éxito
                    onSuccess()
                },
                onFailure = { error ->
                    _isSubmitting = false
                    // Aquí podrías mostrar un mensaje de error
                    // Por ejemplo, podrías tener un estado de error en el ViewModel
                    println("Error al guardar transacción: $error")
                }
            )

        } catch (e: Exception) {
            _isSubmitting = false
            println("Error inesperado: ${e.message}")
        }
    }

    // Función para resetear el formulario
    fun resetForm() {
        _transactionState = transactionState(
            type = TransactionType.EXPENSE,
            amount = "",
            description = "",
            paymentMethod = "Efectivo",
            selectedDate = Date()
        )
        _amountError = ""
        _descriptionError = ""
        _isSubmitting = false
    }

    // Función para obtener métodos de pago
    fun getPaymentMethods(): List<String> {
        return listOf("Efectivo", "Tarjeta", "Transferencia", "Otro")
    }
}