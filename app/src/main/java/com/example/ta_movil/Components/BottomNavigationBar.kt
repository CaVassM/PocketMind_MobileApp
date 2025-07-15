package com.example.ta_movil.Components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Additionals.ColorsTheme
import com.example.ta_movil.R
import com.example.ta_movil.ViewModels.dashboard.Screen



@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = ColorsTheme.fabColor,
        contentColor = ColorsTheme.headerColor
    ) {
        // Principal
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Principal"
                )
            },
            label = {
                Text(
                    text = "Principal",
                    fontSize = 12.sp,
                    fontWeight = if (currentScreen == Screen.Dashboard) FontWeight.Bold else FontWeight.Normal
                )
            },
            selected = currentScreen == Screen.Dashboard,
            onClick = { onNavigate(Screen.Dashboard) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B4513),
                selectedTextColor = Color(0xFF8B4513),
                unselectedIconColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                unselectedTextColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                indicatorColor = Color(0xFFDEB887)
            )
        )

        // Estadísticas
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.money_bottom),
                    contentDescription = "Historial"
                )
            },
            label = {
                Text(
                    text = "Historial",
                    fontSize = 12.sp,
                    fontWeight = if (currentScreen == Screen.IngresosEgresos) FontWeight.Bold else FontWeight.Normal
                )
            },
            selected = currentScreen == Screen.IngresosEgresos,
            onClick = { onNavigate(Screen.IngresosEgresos) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B4513),
                selectedTextColor = Color(0xFF8B4513),
                unselectedIconColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                unselectedTextColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                indicatorColor = Color(0xFFDEB887)
            )
        )

        // Grupos
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Grupos"
                )
            },
            label = {
                Text(
                    text = "Grupos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            selected = false, // No tienes esta pantalla, así que siempre false
            onClick = { /* No implementado */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B4513),
                selectedTextColor = Color(0xFF8B4513),
                unselectedIconColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                unselectedTextColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                indicatorColor = Color(0xFFDEB887)
            )
        )

        // Historial
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Historial"
                )
            },
            label = {
                Text(
                    text = "Historial",
                    fontSize = 12.sp,
                    fontWeight = if (currentScreen == Screen.Historial) FontWeight.Bold else FontWeight.Normal
                )
            },
            selected = currentScreen == Screen.Historial,
            onClick = { onNavigate(Screen.Historial) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B4513),
                selectedTextColor = Color(0xFF8B4513),
                unselectedIconColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                unselectedTextColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                indicatorColor = Color(0xFFDEB887)
            )
        )

        // Mi Perfil
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Mi Perfil"
                )
            },
            label = {
                Text(
                    text = "Mi Perfil",
                    fontSize = 12.sp,
                    fontWeight = if (currentScreen == Screen.Configuracion) FontWeight.Bold else FontWeight.Normal
                )
            },
            selected = currentScreen == Screen.Configuracion,
            onClick = { onNavigate(Screen.Configuracion) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF8B4513),
                selectedTextColor = Color(0xFF8B4513),
                unselectedIconColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                unselectedTextColor = Color(0xFF8B4513).copy(alpha = 0.6f),
                indicatorColor = Color(0xFFDEB887)
            )
        )
    }
}