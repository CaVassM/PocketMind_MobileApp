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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
                    text = "¡Crea tu Cuenta!",
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

    // Controla los focos sobre cada input luego de presionar "Enter".
    val nameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val confirmPasswordFocus = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

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
        NameField(registerViewModel, focusRequester = nameFocus, nextFocus = emailFocus)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Ingrese su correo electronico")
        EmailFieldRegister(registerViewModel, focusRequester = emailFocus, nextFocus = phoneFocus)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Ingrese su telefono")
        PhoneField(registerViewModel, focusRequester = phoneFocus, nextFocus = passwordFocus)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Ingrese su contraseña")
        PasswordFieldRegister(registerViewModel, focusRequester = passwordFocus, nextFocus = confirmPasswordFocus)
        Spacer(modifier = Modifier.size(16.dp))

        LabelText("Confirme su contraseña")
        ConfirmPasswordField(registerViewModel, focusRequester = confirmPasswordFocus, focusManager = focusManager)
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
fun NameField(registerViewModel: RegisterViewModel, focusRequester: FocusRequester, nextFocus: FocusRequester) {
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.name,
        onValueChange = {
            registerViewModel.onNameInput(it)
        },
        placeholder = { Text(text = "Nombre") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { nextFocus.requestFocus() }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun EmailFieldRegister(registerViewModel: RegisterViewModel, focusRequester: FocusRequester, nextFocus: FocusRequester) {
    // SingleLine para que no se expanda mas.
    // maxLines para indicar que es solo 1
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.email,
        onValueChange = {
            registerViewModel.onEmailInput(it)
        },
        placeholder = { Text(text = "Correo") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { nextFocus.requestFocus() }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun PhoneField(registerViewModel: RegisterViewModel, focusRequester: FocusRequester, nextFocus: FocusRequester) {
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.phoneNumber,
        onValueChange = {
            registerViewModel.onPhoneNumberInput(it)
        },
        placeholder = { Text(text = "Número telefónico") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { nextFocus.requestFocus() }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun PasswordFieldRegister(registerViewModel: RegisterViewModel, focusRequester: FocusRequester, nextFocus: FocusRequester) {
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.

    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.password,
        onValueChange = {
            registerViewModel.onPasswordInput(it)
        },
        placeholder = { Text(text = "Debe contener al menos 8 caracteres") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { nextFocus.requestFocus() }),
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
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun ConfirmPasswordField(registerViewModel: RegisterViewModel, focusRequester: FocusRequester, focusManager: FocusManager) {
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.

    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = registerViewModel.state.confirmPassword,
        onValueChange = {
            registerViewModel.onConfirmPasswordInput(it)
        },
        placeholder = { Text(text = "Contraseña ingresada debe de coincidir") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
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
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

