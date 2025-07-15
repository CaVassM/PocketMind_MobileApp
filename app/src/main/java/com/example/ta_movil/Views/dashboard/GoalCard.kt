package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.ViewModels.dashboard.SavingGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCard(
    goal: SavingGoal,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorsTheme.cardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorsTheme.headerColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${goal.currentAmount} / ${goal.targetAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorsTheme.secondaryText
                    )
                }
                
                // Mostrar progreso como porcentaje
                Text(
                    text = "${String.format("%.1f", (goal.currentAmount / goal.targetAmount * 100).toFloat())}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorsTheme.fabColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de progreso con mejor visualización
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .drawBehind {
                        val radius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        drawRoundRect(
                            color = ColorsTheme.secondaryText.copy(alpha = 0.2f),
                            topLeft = Offset.Zero,
                            size = size,
                            cornerRadius = radius
                        )
                        drawRoundRect(
                            color = ColorsTheme.fabColor,
                            topLeft = Offset.Zero,
                            size = size.copy(
                                width = size.width * (goal.currentAmount / goal.targetAmount).toFloat()
                            ),
                            cornerRadius = radius
                        )
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ColorsTheme.headerColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ColorsTheme.expenseColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}
