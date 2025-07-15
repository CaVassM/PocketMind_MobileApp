package com.example.ta_movil.Views.userLogin.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ta_movil.Components.preLogin.ButtonApp
import com.example.ta_movil.ViewModels.userLogin.ForgotViewModel
import com.example.ta_movil.ui.theme.Nunito
import com.example.ta_movil.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ForgotPassword(
    navController: NavController,
    onSuccess: () -> Unit,
    forgotViewModel: ForgotViewModel,
    auth: FirebaseAuth
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.primaryBackground)
            .padding(AppTheme.paddingLarge),
             horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Spacer(modifier = Modifier.height(300.dp))

        Text(
            text = "¿Olvidaste tu\ncontraseña?",
            color = Color.Black,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 52.sp
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Ingrese su dirección de correo electrónico",
            fontFamily = Nunito,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF513C31)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Campo de email (estilo consistente con Login)
        TextField(
            value = forgotViewModel.state.correo,
            onValueChange = {
                forgotViewModel.onCorreoInput(it)
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppTheme.cornerRadius),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón (estilo consistente)
        Spacer(modifier = Modifier.size(30.dp))
        ButtonApp({
            forgotViewModel.onForgotPassword(auth, onSuccess)
        }, "Enviar código \nde verificación", forgotViewModel.state.buttonEnabled)

        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = "Volver al inicio de sesión",
            fontFamily = Nunito,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF513C31),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.popBackStack() }
                .padding(16.dp)
        )
        if(forgotViewModel.state.errorLabel){
            Text(
                text = forgotViewModel.state.errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

    }
}

