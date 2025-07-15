package com.example.ta_movil.Model.dashboard


import com.example.ta_movil.ViewModels.dashboard.TransactionType
import java.util.Date
/**
 * Data class que representa el estado de una transacción en el formulario
 */
data class transactionState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val description: String = "",
    val paymentMethod: String = "Efectivo",
    val selectedDate: Date = Date()       // <-- Date en vez de String
)
