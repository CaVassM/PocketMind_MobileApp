package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Models.HistorialTransaction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import com.example.ta_movil.ViewModels.dashboard.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    transaction: HistorialTransaction?,
    amount: String,
    description: String,
    date: String,
    paymentMethod: String,
    transactionType: TransactionType,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onSave: () -> Unit
) {
    if (isVisible && transaction != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = ColorsTheme.backgroundColor
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header con el monto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = onAmountChange,
                            label = { Text("Monto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorsTheme.headerColor,
                                focusedLabelColor = ColorsTheme.headerColor,
                                cursorColor = ColorsTheme.headerColor
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "PEN",
                            fontSize = 16.sp,
                            color = ColorsTheme.secondaryText,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Toca el valor para editar",
                            fontSize = 12.sp,
                            color = ColorsTheme.secondaryText
                        )
                    }

                    // Campo de descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorsTheme.headerColor,
                            focusedLabelColor = ColorsTheme.headerColor,
                            cursorColor = ColorsTheme.headerColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción inferior
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ColorsTheme.secondaryText
                            )
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                onSave()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorsTheme.headerColor
                            )
                        ) {
                            Text("Guardar", color = ColorsTheme.backgroundColor)
                        }
                    }
                }
            }
        }
    }
}