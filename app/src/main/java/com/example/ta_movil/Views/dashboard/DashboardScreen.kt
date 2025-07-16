package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.Components.BottomNavigationBar
import com.example.ta_movil.R
import com.example.ta_movil.ViewModels.dashboard.DashboardViewModel
import com.example.ta_movil.ViewModels.dashboard.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel
) {
    val categories = listOf("Todas", "Viaje", "Personal", "Hogar")
    var selectedCategory by remember { mutableStateOf("Todas") }

    LaunchedEffect(Unit) {
        dashboardViewModel.loadSavingGoals()
    }

    Scaffold(
        containerColor = ColorsTheme.backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "¡Tu Ahorro es Excelente!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Menú */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Compartir */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
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
                currentScreen = Screen.Dashboard,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Saludo personalizado
                Text(
                    text = "Bienvenido(a), Carlos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = ColorsTheme.primaryText
                )
            }

            item {
                // Card principal con progreso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ColorsTheme.cardBackground
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Fecha
                        Text(
                            text = "Hoy, 21 de Abril del año 2025",
                            fontSize = 14.sp,
                            color = ColorsTheme.secondaryText,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progreso circular
                        CircularProgressWithCenterText(percentage = 0.35f)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de reporte
                        Button(
                            onClick = { /* Ver reporte */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorsTheme.headerColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Reporte",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ver tu reporte",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            item {
                // Título y filtros de categorías
                Text(
                    text = "Metas de Ahorro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColorsTheme.primaryText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filtros de categorías mejorados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            selected = selectedCategory == category,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColorsTheme.headerColor,
                                selectedLabelColor = Color.White,
                                containerColor = ColorsTheme.cardBackground,
                                labelColor = ColorsTheme.secondaryText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == category,
                                borderColor = ColorsTheme.headerColor.copy(alpha = 0.3f),
                                selectedBorderColor = ColorsTheme.headerColor
                            )
                        )
                    }
                }
            }

            // Lista de metas de ahorro
            items(dashboardViewModel.savingGoals.filter {
                selectedCategory == "Todas" || it.name.contains(selectedCategory, ignoreCase = true)
            }) { goal ->
                GoalCard(goal)
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CircularProgressWithCenterText(
    percentage: Float,
    radius: Dp = 80.dp,
    strokeWidth: Dp = 12.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        Canvas(modifier = Modifier.size(radius * 2)) {
            // Círculo de fondo
            drawArc(
                color = ColorsTheme.secondaryText.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Círculo de progreso
            val sweep = 360 * percentage
            drawArc(
                color = ColorsTheme.headerColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de medalla
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ColorsTheme.headerColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_medal),
                    contentDescription = "Medal Icon",
                    tint = ColorsTheme.headerColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto de progreso
            Text(
                text = "Ahorro:",
                fontSize = 14.sp,
                color = ColorsTheme.secondaryText,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                fontSize = 24.sp,
                color = ColorsTheme.primaryText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
