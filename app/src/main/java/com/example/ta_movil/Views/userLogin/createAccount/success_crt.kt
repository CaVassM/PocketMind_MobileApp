package com.example.ta_movil.Views.userLogin.createAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Components.preLogin.ButtonApp
import com.example.ta_movil.ui.theme.AppTheme


@Composable
fun CreateSuccess(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.primaryBackground)
            .padding(AppTheme.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(200.dp))
        // Círculo de confirmación con el texto "Listo"
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = AppTheme.primaryColor.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .border(
                    width = 5.dp,
                    color = AppTheme.primaryColor,
                    shape = CircleShape
                )
        ) {
            Text(
                text = "✓",
                color = AppTheme.primaryColor,
                fontSize = 75.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "Listo",
            color = Color(0xFF513C31),
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 52.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Tu cuenta ha sido creada",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color(0xFF513C31)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Botón para ir al inicio de sesión
        ButtonApp(
            onNext = onNext, string = "¡Bienvenido a\nnuestra familia!", true
        )
    }
}