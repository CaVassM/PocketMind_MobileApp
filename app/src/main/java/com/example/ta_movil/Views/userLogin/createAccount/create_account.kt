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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Additionals.Dimens
import com.example.ta_movil.Components.preLogin.ButtonApp

import com.example.ta_movil.R
// Reciclados

import com.example.ta_movil.Components.preLogin.ClickableText
import com.example.ta_movil.Components.preLogin.LabelText
import com.example.ta_movil.ViewModels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccount(
    onNext: () -> Unit,
    onPrev: () -> Unit,
    auth: FirebaseAuth,
    registerViewModel: RegisterViewModel
) {
    // Procedemos.
    Scaffold  (
        modifier = Modifier
            .fillMaxWidth(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                title = {Text(
                    text = "!Crea tu Cuenta!",
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
            Create_content(onNext,onPrev, auth, registerViewModel)
        }

    }

}

@Composable
fun Create_content(
    onNext: () -> Unit,
    onPrev: () -> Unit,
    auth: FirebaseAuth,
    registerViewModel: RegisterViewModel
) {
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
        NameField(registerViewModel)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Ingrese su correo electronico")
        EmailFieldRegister(registerViewModel)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Ingrese su telefono")
        PhoneField(registerViewModel)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Ingrese su contraseña")
        PasswordFieldRegister(registerViewModel)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Confirme su contraseña")
        ConfirmPasswordField(registerViewModel)
        Spacer(modifier = Modifier.size(30.dp))

        ButtonApp(
            onNext = {
                registerViewModel.onVerify(onNext, auth)
            },
            "Crear cuenta",
            enabled = registerViewModel.state.buttonEnabled
        )
        Spacer(modifier = Modifier.size(30.dp))
        // Volver al login
        ClickableText(text = "Volver al inicio de sesión", onClick = onPrev)

        // Manejo de errores
        if (registerViewModel.state.errorMessage.isNotEmpty()) {
            Text(
                text = registerViewModel.state.errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

}

@Composable
fun PasswordFieldRegister(registerViewModel: RegisterViewModel) {
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.
    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.password,
        onValueChange = {
            registerViewModel.onPasswordInput(it)
        },
        label = { Text(text = "Contraseña") },
        placeholder = { Text(text = "Debe contener al menos 8 caracteres") },
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
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun EmailFieldRegister(registerViewModel: RegisterViewModel) {
    // SingleLine para que no se expanda mas.
    // maxLines para indicar que es solo 1
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.email,
        onValueChange = {
            registerViewModel.onEmailInput(it)
        },
        label = { Text(text = "Correo") },
        placeholder = { Text(text = "Correo") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun ConfirmPasswordField(registerViewModel: RegisterViewModel) {
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.
    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.confirmPassword,
        onValueChange = {
            registerViewModel.onConfirmPasswordInput(it)
        },
        label = { Text(text = "Confirmar contraseña") },
        placeholder = { Text(text = "Contraseña ingresada debe de coincidir") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Se aplica la transformacion solo si el usuario decide que sea visible.
        trailingIcon = {
            val image = if (passwordVisible) R.drawable.visibility
            else R.drawable.no_visibility
            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = painterResource(image),
                    contentDescription = description
                )

            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun PhoneField(registerViewModel: RegisterViewModel) {
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.phoneNumber,
        onValueChange = {
            registerViewModel.onPhoneNumberInput(it)
        },
        placeholder = { Text(text = "Número telefónico") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun NameField(registerViewModel: RegisterViewModel) {
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.name,
        onValueChange = {
            registerViewModel.onNameInput(it)
        },
        placeholder = { Text(text = "Nombre") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}