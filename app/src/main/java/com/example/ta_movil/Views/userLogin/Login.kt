package com.example.ta_movil.Views.userLogin

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ta_movil.Additionals.Dimens
import com.example.ta_movil.Components.preLogin.ButtonApp
import com.example.ta_movil.R
import com.example.ta_movil.Components.preLogin.ClickableText
import com.example.ta_movil.Components.preLogin.IconGoogle
import com.example.ta_movil.Components.preLogin.LabelText
import com.example.ta_movil.Components.preLogin.LogoComponent
import com.example.ta_movil.ViewModels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(
    onSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit,
    auth: FirebaseAuth,
    loginViewModel: AuthViewModel
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDEB887))
            .padding(
                horizontal = Dimens.paddingLarge,
                vertical = Dimens.paddingSmall
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        LoginContent(onSuccess, onCreateAccount, onForgotPassword, auth, loginViewModel)
    }
}

@Composable
fun LoginContent(
    onSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit,
    auth: FirebaseAuth,
    loginViewModel: AuthViewModel
) {
    // Capturamos el state.
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        LogoComponent()
        // Ahora el de google.
        // Pendiente pasarle evento para iniciar sesión en google.
        IconGoogle()
        Spacer(modifier = Modifier.size(16.dp))

        Text(text = "Ó")
        Spacer(modifier = Modifier.size(16.dp))

        EmailField(loginViewModel)
        Spacer(modifier = Modifier.size(16.dp))

        PasswordField(loginViewModel)
        Spacer(modifier = Modifier.size(48.dp))

        ClickableText(text = "¿No tiene cuenta?", onClick = onCreateAccount)
        Spacer(modifier = Modifier.size(4.dp))

        ClickableText(text = "¿Olvidó su contraseña?", onClick = onForgotPassword)
        Spacer(modifier = Modifier.size(30.dp))

        // el .addONCompleteListener es como tal, un listener de la corrutina signIn.
        // El task que pasa como parámetro es digamos, el resultado del Promise
        ButtonApp(
            onNext = {
                loginViewModel.onLogin(auth, onSuccess)
            },
            string = "Ingresar",
            enabled = loginViewModel.state.isLoginEnabled
        )
        Spacer(modifier = Modifier.size(16.dp))
        if (loginViewModel.state.errorMessage.isNotEmpty()) {
            Text(
                text = loginViewModel.state.errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }




    }
}


@Composable
fun EmailField(loginViewModel: AuthViewModel) {
    // SingleLine para que no se expanda mas.
    // maxLines para indicar que es solo 1
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = loginViewModel.state.email,
        onValueChange = {
            loginViewModel.onEmailInput(it)
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
fun PasswordField(loginViewModel: AuthViewModel) {
    // SingleLine para que no se expanda mas.
    // maxLines para indicar que es solo 1
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.
    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = loginViewModel.state.password,
        onValueChange = {
            loginViewModel.onPasswordInput(it)
        },
        label = { Text(text = "Contraseña") },
        placeholder = { Text(text = "Contraseña") },
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