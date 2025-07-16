package com.example.ta_movil.Views.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ta_movil.ViewModels.dashboard.SavingGoal
import com.example.ta_movil.ViewModels.dashboard.Screen
import com.example.ta_movil.ViewModels.dashboard.TransactionType
import java.util.Locale

import java.text.SimpleDateFormat
import java.util.*

val date = Date()
val locale = Locale("es", "ES")
val formatter = SimpleDateFormat("EEEE, d 'de' MMMM 'del' yyyy", locale)
val formattedDate = formatter.format(date).replaceFirstChar { it.uppercase() }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel
) {
    val categories = listOf("Todas", "Viaje", "Personal", "Hogar")
    var selectedCategory by remember { mutableStateOf("Todas") }

    val transacciones = dashboardViewModel.transactions // si las tienes cargadas allí
    val ingresos = transacciones.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val egresos = transacciones.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val ahorro = ingresos - egresos
    val meta = 5000.0 // o el valor de tu meta general

    val porcentajeAhorro = if (meta > 0) (ahorro / meta).toFloat().coerceIn(0f, 1f) else 0f

    LaunchedEffect(Unit) {
        dashboardViewModel.loadSavingGoals()
    }

    Scaffold(
        containerColor = ColorsTheme.backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "",
                        color = Color.White,
                        fontSize = 24.sp,
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
                        // Screen.Historial -> navController.navigate("historial")
                        Screen.Configuracion -> navController.navigate("configuracion")
                        Screen.Goals -> TODO()
                        Screen.Categorias -> TODO()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Bienvenido(a)",
                style = MaterialTheme.typography.bodyLarge,
                color = ColorsTheme.primaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp), // Puedes ajustar la altura si quieres centrado vertical
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = ColorsTheme.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(formattedDate, fontSize = 14.sp, color = ColorsTheme.secondaryText)
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressWithCenterText(percentage = porcentajeAhorro)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { /* Ver tu reporte */ }) {
                        Text("Ver tu reporte")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categories.forEach { category ->
                    OutlinedButton(
                        onClick = { selectedCategory = category },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedCategory == category) Color(0xFFE5C58F) else Color.White
                        )
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(dashboardViewModel.savingGoals.filter {
                    selectedCategory == "Todas" || it.name.contains(selectedCategory, ignoreCase = true)
                }) { goal ->
                    SavingGoalCard(goal)
                }
            }
        }
    }
}

@Composable
fun CircularProgressWithCenterText(
    percentage: Float,
    radius: Dp = 100.dp,
    strokeWidth: Dp = 20.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        Canvas(modifier = Modifier.size(radius * 2)) {
            val sweep = 360 * percentage
            drawArc(
                color = Color(0xFFE57373),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_medal),
                contentDescription = "Medal Icon",
                tint = Color.Unspecified
            )
            Text("Ahorro:\n${(percentage * 100).toInt()}%", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SavingGoalCard(goal: SavingGoal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = goal.name, fontWeight = FontWeight.Bold, color = ColorsTheme.primaryText)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (goal.currentAmount / goal.targetAmount).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(goal.currentAmount / goal.targetAmount * 100).toInt()}%",
                fontSize = 12.sp,
                color = ColorsTheme.secondaryText
            )
        }
    }
}
