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
import androidx.compose.material.icons.filled.Star
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
import com.example.ta_movil.ViewModels.dashboard.ProfileViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showLogoutDialog() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B4513)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = viewModel.currentScreen,
                onNavigate = { screen ->
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard")
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos")
                        Screen.Categorias -> navController.navigate("categorias")
                        Screen.Configuracion -> navController.navigate("configuracion")
                        Screen.Goals -> navController.navigate("goals")
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
                        color = Color(0xFF513C31),
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
                                fontSize = 40.sp
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

                Text(
                    text = "Lima, Perú",
                    fontSize = 16.sp,
                    color = Color(0xFF8B4513),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campos del perfil
                ProfileField(
                    label = "Email",
                    value = uiState.email,
                    isEditable = uiState.isEditMode,
                    onValueChange = { viewModel.updateEmail(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(
                    label = "Número",
                    value = uiState.phoneNumber,
                    isEditable = uiState.isEditMode,
                    onValueChange = { viewModel.updatePhoneNumber(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(
                    label = "Moneda seleccionada",
                    value = uiState.selectedCurrency,
                    isEditable = uiState.isEditMode,
                    onValueChange = { viewModel.updateSelectedCurrency(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón eliminar cuenta
                TextButton(
                    onClick = { viewModel.showDeleteAccountDialog() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Eliminar cuenta",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Diálogo de cierre de sesión
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLogoutDialog() },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        navController.navigate("auth") {
                            popUpTo(0)
                        }
                    }
                ) {
                    Text("Cerrar sesión", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideLogoutDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de eliminación de cuenta
    if (uiState.showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteAccountDialog() },
            title = { Text("Eliminar cuenta") },
            text = { Text("Esta acción no se puede deshacer. Se eliminarán todos tus datos.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount()
                        navController.navigate("auth") {
                            popUpTo(0)
                        }
                    }
                ) {
                    Text("Eliminar cuenta", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteAccountDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Mostrar error si existe
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí puedes mostrar un Snackbar o Toast
            viewModel.clearErrorMessage()
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit
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
                shape = RoundedCornerShape(12.dp)
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