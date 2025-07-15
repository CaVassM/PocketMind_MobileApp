package com.example.ta_movil.Views.userLogin.createAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import com.example.ta_movil.Components.preLogin.LabelText
import com.example.ta_movil.ViewModels.userLogin.RegisterViewModel
import com.example.ta_movil.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VerifyIdentity(onSuccess: () -> Unit, auth: FirebaseAuth, registerViewModel: RegisterViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.primaryBackground)
            .padding(AppTheme.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Spacer(modifier = Modifier.height(300.dp))

        Text(
            text = "Verifica tu\nidentidad",
            color = Color(0xFF513C31),
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 52.sp
        )

        Spacer(modifier = Modifier.height(40.dp))
        LabelText("Hemos enviado un enlace de verificación a tu correo electrónico")

        Spacer(modifier = Modifier.size(30.dp))
        // Si es que el usuario cuenta con correo verificado, entonces funciona
        ButtonApp(onNext = {
            registerViewModel.onVerifyIdentity(onSuccess, auth)
        }, " Verificar", true)
        if (registerViewModel.state.errorMessageVerify.isNotEmpty()) {
            Text(
                text = registerViewModel.state.errorMessageVerify,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

}