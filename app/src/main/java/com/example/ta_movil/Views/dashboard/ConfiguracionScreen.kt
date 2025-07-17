package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Components.BottomNavigationBar
import com.example.ta_movil.ViewModels.dashboard.AuthSharedViewModel
import com.example.ta_movil.ViewModels.dashboard.ProfileViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    authSharedViewModel: AuthSharedViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorsTheme.backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Menú */ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { authSharedViewModel.showLogoutDialog() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorsTheme.headerColor
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = viewModel.currentScreen,
                onNavigate = { screen ->
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos") {
                            popUpTo("ingresos_egresos") { inclusive = true }
                        }
                        Screen.Categorias -> navController.navigate("categorias") {
                            popUpTo("categorias") { inclusive = true }
                        }
                        Screen.Configuracion -> navController.navigate("configuracion") {
                            popUpTo("configuracion") { inclusive = true }
                        }
                        Screen.Goals -> navController.navigate("goals") {
                            popUpTo("goals") { inclusive = true }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorsTheme.backgroundColor)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Header con fondo curvo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = Color(0xFF795548),
                        shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "@piggysaver",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar con icono de cámara
                    Box(
                        modifier = Modifier.size(80.dp)
                    ) {
                        // Avatar circular
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB347)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🐷",
                                fontSize = 40.sp,
                                color = Color.White
                            )
                        }

                        // Icono de cámara
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF654321))
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Contenido del perfil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nombre y ubicación
                Text(
                    text = uiState.displayName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Campos del perfil
                ProfileField(
                    label = "Email",
                    value = uiState.email,
                    isEditable = uiState.isEditMode,
                    onValueChange = { viewModel.updateEmail(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(
                    label = "Moneda seleccionada",
                    value = uiState.selectedCurrency,
                    isEditable = uiState.isEditMode,
                    onValueChange = { viewModel.updateSelectedCurrency(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Sección de Notificaciones
                Text(
                    text = "Notificaciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notificaciones de transacciones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones de transacciones",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notificaciones de metas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones de metas",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.notificationsGoalsEnabled,
                        onCheckedChange = { viewModel.toggleGoalsNotifications() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón eliminar cuenta
                Button(
                    onClick = { viewModel.showDeleteAccountDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Eliminar cuenta",
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Diálogo de confirmación de eliminación
        if (uiState.showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteAccountDialog() },
                title = { Text("Eliminar cuenta") },
                text = { Text("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteAccount() }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.hideDeleteAccountDialog() }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo de error si existe
        if (uiState.errorMessage != null) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.clearErrorMessage() }
                    ) {
                        Text("Cerrar")
                    }
                }
            ) {
                Text(uiState.errorMessage!!)
            }
        }
    }

    // Diálogo de logout
    val showLogoutDialog by authSharedViewModel.showLogoutDialog.collectAsState()
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { authSharedViewModel.hideLogoutDialog() },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authSharedViewModel.logout(navController)
                    }
                ) {
                    Text("Confirmar", color = ColorsTheme.primaryText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        authSharedViewModel.hideLogoutDialog()
                    }
                ) {
                    Text("Cancelar", color = ColorsTheme.secondaryText)
                }
            }
        )
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8B4513)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditable) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8B4513),
                    unfocusedBorderColor = Color(0xFFD3D3D3)
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = trailingIcon
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF8F8F8),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}