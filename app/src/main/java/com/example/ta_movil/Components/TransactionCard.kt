package com.example.ta_movil.Components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ta_movil.ViewModels.Transaction

@Composable
fun TransactionCard(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${transaction.amount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = transaction.date.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
