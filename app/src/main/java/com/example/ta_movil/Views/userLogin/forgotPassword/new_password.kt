package com.example.ta_movil.Views.userLogin.forgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Components.ButtonApp
import com.example.ta_movil.ui.theme.AppTheme
import com.example.ta_movil.ui.theme.Nunito


@Composable
fun EstablishPassword(
    onSucess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.primaryBackground)
            .padding(AppTheme.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Spacer(modifier = Modifier.height(220.dp))

        Text(
            text = "Verifica tu\nidentidad",
            color = Color.Black,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 52.sp
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Establece una contraseña nueva",
            fontFamily = Nunito,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF513C31)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // POR MIENTRAS VARIABLE LOCAL
        // Nueva contraseña
        val newPasswordState = remember { mutableStateOf("") }
        TextField(
            value = newPasswordState.value,
            onValueChange = { newPasswordState.value = it },
            label = { Text("Nueva Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppTheme.cornerRadius),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppTheme.white,
                focusedContainerColor = AppTheme.white,
                unfocusedLabelColor = AppTheme.primaryText,
                focusedLabelColor = AppTheme.primaryText
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Confirmar contraseña",
            fontFamily = Nunito,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF513C31)
        )
        Spacer(modifier = Modifier.height(24.dp))
        val confirmPasswordState = remember { mutableStateOf("") }
        TextField(
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppTheme.cornerRadius),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppTheme.white,
                focusedContainerColor = AppTheme.white,
                unfocusedLabelColor = AppTheme.primaryText,
                focusedLabelColor = AppTheme.primaryText
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botón (estilo consistente)
        Spacer(modifier = Modifier.size(30.dp))
        ButtonApp(onSucess, "   Cambiar  \n Contraseña")
    }
}
