package com.example.ta_movil.Components.preLogin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.ui.theme.Nunito


@Composable
fun ButtonApp(onNext: () -> Unit, string: String, enabled: Boolean) {
    // El onClick mandará a la pestaña del inicio.
    Button(
        onClick = onNext,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF513C31),     // Fondo del botón (verde)
            contentColor = Color.White              // Color del texto
        ),
        enabled = enabled
    ){
        Text(
            text = string,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
    }
}