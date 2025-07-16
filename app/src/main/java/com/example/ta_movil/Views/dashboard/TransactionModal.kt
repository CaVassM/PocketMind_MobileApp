package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.ViewModels.dashboard.Category
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
    dashboardViewModel: DashboardViewModel
) {
    // Estados del DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    // Obtener el estado del ViewModel
    val transactionState = viewModel.transactionState
    val amountError = viewModel.amountError
    val descriptionError = viewModel.descriptionError
    val categoryError = viewModel.categoryError
    val isSubmitting = viewModel.isSubmitting
    val categories = viewModel.categories
    val selectedCategoryId = viewModel.selectedCategoryId
    val isLoadingCategories = viewModel.isLoadingCategories

    // Opciones de métodos de pago
    val paymentMethods = viewModel.getPaymentMethods()

    // Función para guardar la transacción
    fun saveTransaction() {
        viewModel.saveTransaction(
            dashboardViewModel = dashboardViewModel,
            onSuccess = {
                dashboardViewModel.loadTransactions()
                onDismiss()
            }
        )
    }

    // Resetear errores cuando se cierre el modal
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
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
                    }

                    item {
                        // Selector de tipo de transacción
                        Text(
                            text = "Tipo de Transacción",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorsTheme.primaryText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón para Ingreso
                            FilterChip(
                                onClick = { viewModel.updateTransactionType(TransactionType.INCOME) },
                                label = {
                                    Text(
                                        "Ingreso",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                selected = transactionState.type == TransactionType.INCOME,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ColorsTheme.incomeColor.copy(alpha = 0.15f),
                                    selectedLabelColor = ColorsTheme.incomeColor,
                                    labelColor = ColorsTheme.secondaryText,
                                    containerColor = ColorsTheme.cardBackground
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            // Botón para Gasto
                            FilterChip(
                                onClick = { viewModel.updateTransactionType(TransactionType.EXPENSE) },
                                label = {
                                    Text(
                                        "Gasto",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                selected = transactionState.type == TransactionType.EXPENSE,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ColorsTheme.expenseColor.copy(alpha = 0.15f),
                                    selectedLabelColor = ColorsTheme.expenseColor,
                                    labelColor = ColorsTheme.secondaryText,
                                    containerColor = ColorsTheme.cardBackground
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        // Campo de monto
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
                                focusedLabelColor = ColorsTheme.headerColor,
                                unfocusedBorderColor = ColorsTheme.secondaryText.copy(alpha = 0.5f),
                                unfocusedLabelColor = ColorsTheme.secondaryText,
                                focusedTextColor = ColorsTheme.primaryText,
                                unfocusedTextColor = ColorsTheme.primaryText
                            )
                        )
                    }

                    item {
                        // Campo de descripción
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
                                focusedLabelColor = ColorsTheme.headerColor,
                                unfocusedBorderColor = ColorsTheme.secondaryText.copy(alpha = 0.5f),
                                unfocusedLabelColor = ColorsTheme.secondaryText,
                                focusedTextColor = ColorsTheme.primaryText,
                                unfocusedTextColor = ColorsTheme.primaryText
                            )
                        )
                    }

                    item {
                        // Selector de categoría
                        Text(
                            text = "Categoría",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorsTheme.primaryText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (isLoadingCategories) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = ColorsTheme.headerColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            CategorySelector(
                                categories = categories,
                                selectedCategoryId = selectedCategoryId,
                                onCategorySelected = { viewModel.updateSelectedCategory(it) },
                                isError = categoryError.isNotEmpty(),
                                errorMessage = categoryError
                            )
                        }
                    }

                    item {
                        // Selector de método de pago
                        Text(
                            text = "Método de Pago",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorsTheme.primaryText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = ColorsTheme.cardBackground
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                paymentMethods.forEach { method ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .selectable(
                                                selected = transactionState.paymentMethod == method,
                                                onClick = { viewModel.updatePaymentMethod(method) }
                                            )
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = transactionState.paymentMethod == method,
                                            onClick = { viewModel.updatePaymentMethod(method) },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = ColorsTheme.headerColor,
                                                unselectedColor = ColorsTheme.secondaryText
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = method,
                                            color = ColorsTheme.primaryText,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Selector de fecha
                        Text(
                            text = "Fecha",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorsTheme.primaryText,
                            modifier = Modifier.padding(bottom = 8.dp)
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
                                focusedLabelColor = ColorsTheme.headerColor,
                                unfocusedBorderColor = ColorsTheme.secondaryText.copy(alpha = 0.5f),
                                unfocusedLabelColor = ColorsTheme.secondaryText,
                                focusedTextColor = ColorsTheme.primaryText,
                                unfocusedTextColor = ColorsTheme.primaryText
                            )
                        )
                    }

                    item {
                        // Botones de acción
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botón cancelar
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ColorsTheme.secondaryText
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        ColorsTheme.secondaryText.copy(alpha = 0.5f)
                                    )
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
                                    containerColor = ColorsTheme.headerColor,
                                    contentColor = Color.White
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
    }

    // DatePicker
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
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    Column {
        // Campo de selección de categoría
        OutlinedTextField(
            value = selectedCategory?.name ?: "Seleccionar categoría",
            onValueChange = { },
            label = { Text("Categoría") },
            readOnly = true,
            isError = isError,
            supportingText = if (isError && errorMessage.isNotEmpty()) {
                { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
            } else null,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Contraer categorías" else "Expandir categorías",
                        tint = ColorsTheme.headerColor
                    )
                }
            },
            leadingIcon = selectedCategory?.let { category ->
                {
                    CategoryIcon(
                        category = category,
                        size = 24.dp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorsTheme.headerColor,
                focusedLabelColor = ColorsTheme.headerColor,
                unfocusedBorderColor = ColorsTheme.secondaryText.copy(alpha = 0.5f),
                unfocusedLabelColor = ColorsTheme.secondaryText,
                focusedTextColor = ColorsTheme.primaryText,
                unfocusedTextColor = ColorsTheme.primaryText
            )
        )

        // Dropdown de categorías con mejor styling
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ColorsTheme.cardBackground,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            if (categories.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No hay categorías disponibles",
                            color = ColorsTheme.secondaryText,
                            fontSize = 14.sp
                        )
                    },
                    onClick = { }
                )
            } else {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryIcon(
                                    category = category,
                                    size = 28.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = category.name,
                                    color = ColorsTheme.primaryText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        onClick = {
                            onCategorySelected(category.id)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (selectedCategoryId == category.id) {
                                    ColorsTheme.headerColor.copy(alpha = 0.1f)
                                } else {
                                    Color.Transparent
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryIcon(
    category: Category,
    size: androidx.compose.ui.unit.Dp
) {
    // Convertir el color hexadecimal a Color
    val color = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (e: Exception) {
        ColorsTheme.headerColor // Color por defecto más atractivo
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        // Aquí deberías usar el icono real basado en category.icon
        // Por ahora uso un icono genérico con mejor apariencia
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_info_details),
            contentDescription = category.name,
            tint = color,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

// Mantener las funciones del DatePicker igual que antes
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