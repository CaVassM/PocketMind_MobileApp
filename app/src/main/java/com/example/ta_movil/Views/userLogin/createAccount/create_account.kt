package com.example.ta_movil.Views.userLogin.createAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Additionals.Dimens

import com.example.ta_movil.R
// Reciclados
import com.example.ta_movil.Views.userLogin.EmailField
import com.example.ta_movil.Views.userLogin.PasswordField
import com.example.ta_movil.Components.preLogin.ButtonApp

import com.example.ta_movil.Components.preLogin.ClickableText
import com.example.ta_movil.Components.preLogin.LabelText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccount(onNext: () -> Unit, onPrev: () -> Unit) {
    // Procedemos.
    Scaffold  (
        modifier = Modifier
            .fillMaxWidth(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                title = {Text(
                    text = "Crear cuenta",
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    color = Color(0xFF513C31)
                )},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.White
    ){ innerPadding ->
        // Se crea el content create_account
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFDEB887))
                .padding(
                    innerPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Create_content(onNext,onPrev)
        }

    }

}

@Composable
fun Create_content(onNext: () -> Unit, onPrev: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.paddingLarge,
                vertical = Dimens.paddingSmall
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        LabelText("Ingrese su nombre de usuario")
        NameField()
        Spacer(modifier = Modifier.size(16.dp))
        LabelText("Ingrese su correo electronico")
        EmailField()
        Spacer(modifier = Modifier.size(16.dp))
        LabelText("Ingrese su telefono")
        PhoneField()
        Spacer(modifier = Modifier.size(16.dp))
        LabelText("Ingrese su contraseña")
        PasswordField()
        Spacer(modifier = Modifier.size(16.dp))
        LabelText("Confirme su contraseña")
        ConfirmPasswordField()
        Spacer(modifier = Modifier.size(30.dp))
        ButtonApp(
            onNext = onNext,
            string = "Verifica correo"
        )
        Spacer(modifier = Modifier.size(30.dp))
        ClickableText(text = "Volver al inicio de sesión", onClick = onPrev)
    }

}

@Composable
fun ConfirmPasswordField() {
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.
    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = "",
        onValueChange = {  },
        label = { Text(text = "Confirmar contraseña") },
        placeholder = { Text(text = "Confirmar contraseña") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Se aplica la transformacion solo si el usuario decide que sea visible.
        trailingIcon = {
            val image = if (passwordVisible) R.drawable.visibility
            else R.drawable.no_visibility
            // Ya capturado el icono a renderizar, lo enviamos.
            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            // el Click se encarga de alternar si es visible o no.
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = painterResource(image),
                    contentDescription = description
                )

            }
        },
        colors = TextFieldDefaults.colors(
            // --- Para quitar la línea cuando está enfocado ---
            focusedIndicatorColor = Color.Transparent,
            // --- Para quitar la línea cuando no está enfocado pero tiene contenido ---
            unfocusedIndicatorColor = Color.Transparent,
            // --- Para quitar la línea cuando está deshabilitado ---
            disabledIndicatorColor = Color.Transparent,
            // --- Para cambiar el color del fondo ---
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun PhoneField() {
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = "",
        onValueChange = {},
        label = { Text(text = "Telefono") },
        placeholder = { Text(text = "Telefono") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        colors = TextFieldDefaults.colors(
            // --- Para quitar la línea cuando está enfocado ---
            focusedIndicatorColor = Color.Transparent,
            // --- Para quitar la línea cuando no está enfocado pero tiene contenido ---
            unfocusedIndicatorColor = Color.Transparent,
            // --- Para quitar la línea cuando está deshabilitado ---
            disabledIndicatorColor = Color.Transparent,
            // --- Para cambiar el color del fondo ---
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun NameField() {
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = "",
        onValueChange = {},
        label = { Text(text = "Nombre") },
        placeholder = { Text(text = "Nombre") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            // --- Para quitar la línea cuando está enfocado ---
            focusedIndicatorColor = Color.Transparent,
            // --- Para quitar la línea cuando no está enfocado pero tiene contenido ---
            unfocusedIndicatorColor = Color.Transparent,
            // --- Para quitar la línea cuando está deshabilitado ---
            disabledIndicatorColor = Color.Transparent,
            // --- Para cambiar el color del fondo ---
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}