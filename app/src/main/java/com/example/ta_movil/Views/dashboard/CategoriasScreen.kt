package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ta_movil.ViewModels.dashboard.CategoriasViewModel
import com.example.ta_movil.ViewModels.dashboard.Category
import com.example.ta_movil.ViewModels.dashboard.CategoriasUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Components.BottomNavigationBar
import com.example.ta_movil.ViewModels.dashboard.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel = viewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Inicializar categorías cuando se abra la pantalla
    LaunchedEffect(Unit) {
        viewModel.initializeUserCategories()
    }

    // Manejar errores
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarMessage = uiState.error.toString()
            showErrorSnackbar = true
        }
    }

    Scaffold(
        containerColor = ColorsTheme.backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Categorías",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Acción del menú */ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción de compartir */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir",
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
                    viewModel.navigateTo(screen)
                    when (screen) {
                        Screen.Dashboard -> navController.navigate("dashboard")
                        Screen.IngresosEgresos -> navController.navigate("ingresos_egresos")
                        Screen.Categorias -> navController.navigate("categorias")
                        Screen.Configuracion -> navController.navigate("configuracion")
                        Screen.Goals -> navController.navigate("goals")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() }) }
    ) { paddingValues ->
        // Mostrar Snackbar de error
        if (showErrorSnackbar && snackbarMessage.isNotEmpty()) {
            LaunchedEffect(snackbarMessage) {
                kotlinx.coroutines.delay(3000)
                showErrorSnackbar = false
                viewModel.clearError()
            }
        }

        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5E6D3)) // Color beige principal
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header con balance general
            BalanceHeader(uiState = uiState)

            Spacer(modifier = Modifier.height(24.dp))

            // Título con contador de categorías
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categorías",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${uiState.categories.size} categorías",
                    color = Color(0xFF8B4513),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid de categorías
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B4513))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Categorías existentes
                    items(uiState.categories) { category ->
                        CategoryCard(
                            category = category,
                            onEdit = { categoryId ->
                                // TODO: Implementar edición de categoría
                                // Podrías abrir un diálogo similar al de agregar
                            },
                            onDelete = { categoryId ->
                                viewModel.deleteCategory(categoryId)
                            }
                        )
                    }

                    // Botón para agregar nueva categoría
                    item {
                        AddCategoryCard(
                            onAddCategory = { name, icon, color ->
                                viewModel.addCustomCategory(name, icon, color)
                            }
                        )
                    }
                }
            }
        }
    }

    // Snackbar para mostrar errores
    if (showErrorSnackbar) {
        LaunchedEffect(Unit) {
            // El snackbar se mostrará automáticamente
        }
    }
}

@Composable
fun BalanceHeader(uiState: CategoriasUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8D5C4)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Balance general",
                color = Color.Black,
                fontSize = 16.sp
            )

            Text(
                text = "${if (uiState.overallBalance >= 0) "" else "-"}${String.format("%.0f", kotlin.math.abs(uiState.overallBalance))} PEN",
                color = if (uiState.overallBalance >= 0) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Gastos
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCC5B0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gastos",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${String.format("%.0f", uiState.totalExpenses)} PEN",
                            color = Color(0xFFD32F2F),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Ingresos
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCC5B0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ingresos",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${String.format("%.0f", uiState.totalIncome)} PEN",
                            color = Color(0xFF2E7D32),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8D5C4)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Contenido principal de la tarjeta
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clickable { onEdit(category.id) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icono
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = try {
                                Color(android.graphics.Color.parseColor(category.color))
                            } catch (e: Exception) {
                                Color(0xFFDCC5B0)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon.ifEmpty { "📁" },
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Nombre de la categoría
                Text(
                    text = category.name,
                    color = Color(0xFF8B4513),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )

                // Monto
                Text(
                    text = "${String.format("%.0f", category.totalAmount)} PEN",
                    color = if (category.totalAmount > 0) Color(0xFF2E7D32) else Color(0xFF666666),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                // Indicador Pro para categorías personalizadas
                if (!category.isDefault) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFF8B4513),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Añadido",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Menú de opciones (solo para categorías no predeterminadas)
            if (!category.isDefault) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = { showDropdownMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = Color(0xFF8B4513),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false },
                        modifier = Modifier.background(Color(0xFFE8D5C4))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color(0xFF8B4513),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Editar",
                                        color = Color(0xFF8B4513),
                                        fontSize = 14.sp
                                    )
                                }
                            },
                            onClick = {
                                showDropdownMenu = false
                                onEdit(category.id)
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Eliminar",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 14.sp
                                    )
                                }
                            },
                            onClick = {
                                showDropdownMenu = false
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmation) {
        DeleteCategoryDialog(
            categoryName = category.name,
            onConfirm = {
                onDelete(category.id)
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

@Composable
fun DeleteCategoryDialog(
    categoryName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Eliminar Categoría",
                color = Color(0xFF8B4513),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar la categoría \"$categoryName\"?\n\nEsta acción no se puede deshacer.",
                color = Color.Black
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF8B4513)
                )
            ) {
                Text("Cancelar")
            }
        },
        containerColor = Color(0xFFE8D5C4)
    )
}

@Composable
fun AddCategoryCard(
    onAddCategory: (String, String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8D5C4)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir",
                tint = Color(0xFF8B4513),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Añadir",
                color = Color(0xFF8B4513),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Categoría",
                color = Color(0xFF8B4513),
                fontSize = 12.sp
            )
        }
    }

    if (showDialog) {
        AddCategoryDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, icon, color ->
                onAddCategory(name, icon, color)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("📁") }
    var selectedColor by remember { mutableStateOf(Color(0xFF5733FF)) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val availableIcons = listOf(
        "📁", "📊", "💰", "🍔", "🚗", "🏠", "🎮", "📚", "💊", "🛒",
        "✈️", "🏥", "🎵", "👕", "⚽", "🎬", "📱", "💻", "🍕", "☕",
        "🎨", "🏋️", "🌟", "❤️", "💡", "🔧", "🌱", "🎯", "🎪", "🌈"
    )

    val availableColors = listOf(
        Color(0xFF5733FF), Color(0xFF33FF57), Color(0xFFFF3357), Color(0xFFFFD700),
        Color(0xFF8A2BE2), Color(0xFF00CED1), Color(0xFFFF6347), Color(0xFF32CD32),
        Color(0xFFFF1493), Color(0xFF4169E1), Color(0xFFFF8C00), Color(0xFF9370DB),
        Color(0xFF20B2AA), Color(0xFFDC143C), Color(0xFF228B22), Color(0xFFB22222)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar Nueva Categoría",
                color = Color(0xFF8B4513),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Categoría", color = Color(0xFF8B4513)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF8B4513),
                        unfocusedTextColor = Color(0xFF8B4513),
                        focusedBorderColor = Color(0xFF8B4513),
                        unfocusedBorderColor = Color(0xFF8B4513)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de ícono
                Text(
                    text = "Ícono",
                    color = Color(0xFF8B4513),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showIconPicker = !showIconPicker }
                        .border(
                            1.dp,
                            Color(0xFF8B4513),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedIcon,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Seleccionar ícono",
                        color = Color(0xFF8B4513),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(android.R.drawable.arrow_down_float),
                        contentDescription = "Expandir",
                        tint = Color(0xFF8B4513)
                    )
                }

                if (showIconPicker) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableIcons) { icon ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        selectedIcon = icon
                                        showIconPicker = false
                                    }
                                    .background(
                                        if (selectedIcon == icon) Color(0xFF8B4513).copy(alpha = 0.2f)
                                        else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        if (selectedIcon == icon) 2.dp else 1.dp,
                                        if (selectedIcon == icon) Color(0xFF8B4513) else Color.Gray,
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = icon,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de color
                Text(
                    text = "Color",
                    color = Color(0xFF8B4513),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showColorPicker = !showColorPicker }
                        .border(
                            1.dp,
                            Color(0xFF8B4513),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(selectedColor, CircleShape)
                            .border(1.dp, Color.Gray, CircleShape)
                    )
                    Text(
                        text = "Seleccionar color",
                        color = Color(0xFF8B4513),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                    Icon(
                        painter = painterResource(android.R.drawable.arrow_down_float),
                        contentDescription = "Expandir",
                        tint = Color(0xFF8B4513)
                    )
                }

                if (showColorPicker) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableColors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        selectedColor = color
                                        showColorPicker = false
                                    }
                                    .background(color, CircleShape)
                                    .border(
                                        if (selectedColor == color) 3.dp else 1.dp,
                                        if (selectedColor == color) Color.Black else Color.Gray,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val colorHex = String.format("#%08X", selectedColor.toArgb())
                        onConfirm(name, selectedIcon, colorHex)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF8B4513)
                )
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF8B4513)
                )
            ) {
                Text("Cancelar")
            }
        },
        containerColor = Color(0xFFE8D5C4)
    )
}

// Función de extensión para convertir Color a ARGB
fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}