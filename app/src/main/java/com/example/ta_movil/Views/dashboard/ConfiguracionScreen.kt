package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.ta_movil.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.example.ta_movil.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavController,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Información de la cuenta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.primaryBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información de la cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.primaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Correo electrónico: ${auth.currentUser?.email ?: "No disponible"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.primaryText
                    )
                }
            }

            // Preferencias
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.primaryBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Preferencias",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.primaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Cambiar contraseña
                    OutlinedButton(
                        onClick = { /* Implementar cambio de contraseña */ }
                    ) {
                        Text("Cambiar contraseña")
                    }
                    
                    // Cambiar correo
                    OutlinedButton(
                        onClick = { /* Implementar cambio de correo */ }
                    ) {
                        Text("Cambiar correo electrónico")
                    }
                }
            }

            // Seguridad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.primaryBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Seguridad",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.primaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Eliminar cuenta
                    Button(
                        onClick = { showDeleteAccountDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar cuenta")
                    }
                }
            }
        }
    }

    // Diálogo de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth.signOut()
                        navController.navigate("auth") {
                            popUpTo(0)
                        }
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de eliminación de cuenta
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Eliminar cuenta") },
            text = {
                Column {
                    Text("Esta acción no se puede deshacer. Se eliminarán todos tus datos.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Contraseña actual")
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (currentPassword.isNotBlank()) {
                            // Implementar eliminación de cuenta
                            showDeleteAccountDialog = false
                        }
                    }
                ) {
                    Text("Eliminar cuenta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
