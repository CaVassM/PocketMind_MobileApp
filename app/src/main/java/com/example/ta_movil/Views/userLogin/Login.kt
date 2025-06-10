package com.example.ta_movil.Views.userLogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Additionals.Dimens
import com.example.ta_movil.Components.ButtonApp
import com.example.ta_movil.R
import com.example.ta_movil.ui.theme.Nunito

@Composable
fun Login(onSuccess: () -> Unit, onCreateAccount: () -> Unit, onForgotPassword: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })


    // No se usa un topBar ni bottomBar evidentemente.
    // No se usara lazyColumn
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
        // Para lograr un carrusel de imagenes, usar HorizontalPager
        LoginContent(onSuccess, onCreateAccount, onForgotPassword)
    }
}

@Composable
fun LoginContent(onSuccess: () -> Unit, onCreateAccount: () -> Unit, onForgotPassword: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .size(
                    Dimens.imageSize
                ),
            contentScale = ContentScale.Fit
        )
        // Ahora el de google.
        IconGoogle()
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Ó"
        )
        Spacer(modifier = Modifier.size(16.dp))
        EmailField()
        Spacer(modifier = Modifier.size(16.dp))
        PasswordField()
        Spacer(modifier = Modifier.size(48.dp))
        Text(
            text = "¿No tiene cuenta?",
            fontFamily = Nunito,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            color = Color(0xFF513C31)
        )
        Text(
            text = "¿Olvidó su contraseña?",
            fontFamily = Nunito,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            color = Color(0xFF513C31),
            modifier = Modifier
                .clickable(onClick = onForgotPassword) // Esto hace que sea clickable
                .padding(4.dp) // Pequeño padding para mejor UX
        )
        Spacer(modifier = Modifier.size(30.dp))
        ButtonApp(onSuccess, "Ingresar")


    }
}

@Composable
fun IconGoogle() {
    Box(
        modifier = Modifier
            .size(width = 167.dp, height = 30.dp)
            .background(
                color = Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ){
        // Linea
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 1.dp,
            color = Color(0xFF4E3B2F)
        )
        // Ahora el icon.
        IconButton(
            onClick = { /* Pendiente para que se conecte con Google */ },
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(Color.White)

        ) {
            Image(
                painter = painterResource(R.drawable.google_icon),
                contentDescription = "Google"
            )
        }
    }

}

@Composable
fun EmailField() {
    // SingleLine para que no se expanda mas.
    // maxLines para indicar que es solo 1
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = "",
        onValueChange = {},
        label = { Text(text = "Correo") },
        placeholder = { Text(text = "Correo") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
        )
    )
}

@Composable
fun PasswordField() {
    // SingleLine para que no se expanda mas.
    // maxLines para indicar que es solo 1
    var passwordVisible by remember { mutableStateOf(false) } // Para poder alternar entre visible o no.
    // VisualTransformation se encarga de formatear todo el texto del field. Esto implica que para password se vea como puntitos o asi: ***
    TextField(
        shape = RoundedCornerShape(16.dp),
        value = "",
        onValueChange = {  },
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
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )

}