package com.example.ta_movil.Models

import androidx.compose.ui.graphics.Color
import com.example.ta_movil.ViewModels.dashboard.TransactionType
import com.google.firebase.firestore.DocumentReference

/**
 * Modelo para agrupar transacciones por fecha
 */
data class TransactionGroup(
    val date: String,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val totalAmount: Double,
    val transactions: List<HistorialTransaction>
)

/**
 * Modelo para representar una transacción en el historial
 */
data class HistorialTransaction(
    val id: String,
    val description: String,
    val categoryId: DocumentReference?,
    val amount: Double,
    val type: TransactionType,
    val icon: Int,
    val iconColor: Color,
    val paymentMethod: String
)