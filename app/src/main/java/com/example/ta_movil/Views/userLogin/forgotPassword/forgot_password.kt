package com.example.ta_movil.Views.userLogin.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ta_movil.Additionals.Dimens
import com.example.ta_movil.Components.ButtonApp
import com.example.ta_movil.R
import com.example.ta_movil.ui.theme.Nunito
import com.example.ta_movil.ui.theme.AppTheme


@Composable
fun ForgotPassword(
    navController: NavController,
    onSucess: () -> Unit
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
            value = "",
            onValueChange = {},
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppTheme.cornerRadius),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppTheme.white,
                focusedContainerColor = AppTheme.white,
                unfocusedLabelColor = AppTheme.primaryText,
                focusedLabelColor = AppTheme.primaryText
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón (estilo consistente)
        Spacer(modifier = Modifier.size(30.dp))
        ButtonApp(onSucess, "Enviar código \nde verificación")

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

    }
}

