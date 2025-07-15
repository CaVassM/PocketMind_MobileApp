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
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.HistorialModalViewModel
import com.example.ta_movil.ViewModels.dashboard.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: HistorialModalViewModel,
    dashboardViewModel: DashboardViewModel // AGREGADO: Pasar el DashboardViewModel
) {
    // Estados del DatePicker
    var showDatePicker by remember { mutableStateOf(false) }

    // Obtener el estado del ViewModel
    val transactionState = viewModel.transactionState
    val amountError = viewModel.amountError
    val descriptionError = viewModel.descriptionError
    val isSubmitting = viewModel.isSubmitting

    // Opciones de métodos de pago
    val paymentMethods = viewModel.getPaymentMethods()

    // Función para guardar la transacción - CORREGIDA
    fun saveTransaction() {
        viewModel.saveTransaction(
            dashboardViewModel = dashboardViewModel, // AGREGADO: Pasar el DashboardViewModel
            onSuccess = {
                onDismiss()
            }
        )
    }

    // Resetear errores cuando se cierra el modal
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            viewModel.resetForm()
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
                            onClick = { viewModel.updateTransactionType(TransactionType.INCOME) },
                            label = { Text("Ingreso") },
                            selected = transactionState.type == TransactionType.INCOME,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColorsTheme.incomeColor.copy(alpha = 0.2f),
                                selectedLabelColor = ColorsTheme.incomeColor
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Botón para Gasto
                        FilterChip(
                            onClick = { viewModel.updateTransactionType(TransactionType.EXPENSE) },
                            label = { Text("Gasto") },
                            selected = transactionState.type == TransactionType.EXPENSE,
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
                            value = transactionState.amount,
                            onValueChange = { viewModel.updateAmount(it) },
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
                            value = transactionState.description,
                            onValueChange = { viewModel.updateDescription(it) },
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
                                        selected = transactionState.paymentMethod == method,
                                        onClick = { viewModel.updatePaymentMethod(method) }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = transactionState.paymentMethod == method,
                                    onClick = { viewModel.updatePaymentMethod(method) },
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
                        value = viewModel.formatDate(transactionState.selectedDate),
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
                viewModel.updateSelectedDate(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = transactionState.selectedDate
        )
    }
}


@Composable
fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Date
) {
    val context = LocalContext.current

    ShowNativeDatePicker(
        context = context,
        initialDate = initialDate,
        onDateSelected = onDateSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun ShowNativeDatePicker(
    context: Context,
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }

    DisposableEffect(Unit) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, dayOfMonth, 0, 0, 0)
                selectedCal.set(Calendar.MILLISECOND, 0)
                onDateSelected(selectedCal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setOnCancelListener { onDismiss() }
        datePickerDialog.setOnDismissListener { onDismiss() }

        datePickerDialog.show()

        onDispose {
            datePickerDialog.dismiss()
        }
    }
}
