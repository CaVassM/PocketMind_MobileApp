package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.ViewModels.DashboardViewModel
import com.example.ta_movil.ViewModels.Transaction
import com.example.ta_movil.ViewModels.TransactionType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modal para registrar nuevas transacciones
 * Este componente permite al usuario ingresar todos los datos necesarios
 * para crear una nueva transacción y la guarda en Firestore
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: DashboardViewModel
) {
    // Estados para manejar los datos del formulario
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Efectivo") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Estados para validación y mensajes de error
    var amountError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Opciones de métodos de pago
    val paymentMethods = listOf("Efectivo", "Tarjeta", "Transferencia", "Otro")

    // Función para validar los datos del formulario
    fun validateForm(): Boolean {
        var isValid = true

        // Validar monto
        if (amount.isBlank()) {
            amountError = "El monto es requerido"
            isValid = false
        } else {
            try {
                val amountValue = amount.toDouble()
                if (amountValue <= 0) {
                    amountError = "El monto debe ser mayor a 0"
                    isValid = false
                } else {
                    amountError = ""
                }
            } catch (e: NumberFormatException) {
                amountError = "Ingrese un monto válido"
                isValid = false
            }
        }

        // Validar descripción
        if (description.isBlank()) {
            descriptionError = "La descripción es requerida"
            isValid = false
        } else if (description.length < 3) {
            descriptionError = "La descripción debe tener al menos 3 caracteres"
            isValid = false
        } else {
            descriptionError = ""
        }

        return isValid
    }

    // Función para formatear la fecha
    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    // Función para guardar la transacción
    fun saveTransaction() {
        if (!validateForm()) return

        isSubmitting = true

        // Crear el objeto Transaction con un ID único
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            type = transactionType,
            amount = amount.toDouble(),
            description = description.trim(),
            date = formatDate(selectedDate),
            paymentMethod = paymentMethod
        )

        // Guardar en Firestore usando el ViewModel
        viewModel.addTransaction(transaction)

        // Resetear el formulario
        amount = ""
        description = ""
        paymentMethod = "Efectivo"
        selectedDate = Date()
        transactionType = TransactionType.EXPENSE
        isSubmitting = false

        // Cerrar el modal
        onDismiss()
    }

    // Resetear errores cuando se cierra el modal
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            amountError = ""
            descriptionError = ""
            isSubmitting = false
        }
    }

    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ColorsTheme.cardBackground
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header del modal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nueva Transacción",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorsTheme.primaryText
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = ColorsTheme.secondaryText
                            )
                        }
                    }

                    // Selector de tipo de transacción
                    Text(
                        text = "Tipo de Transacción",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorsTheme.primaryText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón para Ingreso
                        FilterChip(
                            onClick = { transactionType = TransactionType.INCOME },
                            label = { Text("Ingreso") },
                            selected = transactionType == TransactionType.INCOME,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColorsTheme.incomeColor.copy(alpha = 0.2f),
                                selectedLabelColor = ColorsTheme.incomeColor
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Botón para Gasto
                        FilterChip(
                            onClick = { transactionType = TransactionType.EXPENSE },
                            label = { Text("Gasto") },
                            selected = transactionType == TransactionType.EXPENSE,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColorsTheme.expenseColor.copy(alpha = 0.2f),
                                selectedLabelColor = ColorsTheme.expenseColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Campo de monto
                    Column {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = {
                                amount = it
                                amountError = "" // Limpiar error al escribir
                            },
                            label = { Text("Monto") },
                            placeholder = { Text("0.00") },
                            prefix = { Text("S/ ") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            isError = amountError.isNotEmpty(),
                            supportingText = if (amountError.isNotEmpty()) {
                                { Text(amountError, color = MaterialTheme.colorScheme.error) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorsTheme.headerColor,
                                focusedLabelColor = ColorsTheme.headerColor
                            )
                        )
                    }

                    // Campo de descripción
                    Column {
                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                descriptionError = "" // Limpiar error al escribir
                            },
                            label = { Text("Descripción") },
                            placeholder = { Text("Ej: Compra de supermercado") },
                            isError = descriptionError.isNotEmpty(),
                            supportingText = if (descriptionError.isNotEmpty()) {
                                { Text(descriptionError, color = MaterialTheme.colorScheme.error) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorsTheme.headerColor,
                                focusedLabelColor = ColorsTheme.headerColor
                            )
                        )
                    }

                    // Selector de método de pago
                    Text(
                        text = "Método de Pago",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorsTheme.primaryText
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = paymentMethod == method,
                                        onClick = { paymentMethod = method }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = method },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = ColorsTheme.headerColor
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method,
                                    color = ColorsTheme.primaryText
                                )
                            }
                        }
                    }

                    // Selector de fecha
                    Text(
                        text = "Fecha",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorsTheme.primaryText
                    )

                    OutlinedTextField(
                        value = formatDate(selectedDate),
                        onValueChange = { },
                        label = { Text("Fecha") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha",
                                    tint = ColorsTheme.headerColor
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorsTheme.headerColor,
                            focusedLabelColor = ColorsTheme.headerColor
                        )
                    )

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón cancelar
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ColorsTheme.secondaryText
                            )
                        ) {
                            Text("Cancelar")
                        }

                        // Botón guardar
                        Button(
                            onClick = { saveTransaction() },
                            modifier = Modifier.weight(1f),
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorsTheme.headerColor
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    // DatePicker (implementación básica)
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = selectedDate
        )
    }
}

/**
 * Componente auxiliar para el selector de fecha
 * En una implementación real, podrías usar una librería como
 * ComposeCalendar o el DatePicker nativo de Material 3
 */
@Composable
fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Date
) {
    // Esta es una implementación simplificada
    // En producción, usarías el DatePicker de Material 3 o una librería especializada
    val calendar = Calendar.getInstance()
    calendar.time = initialDate

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleccionar Fecha",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Fecha actual: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(initialDate)}",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = { onDateSelected(initialDate) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorsTheme.headerColor
                        )
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}