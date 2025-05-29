package com.example.ta_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ta_movil.Components.Navigator
import com.example.ta_movil.ui.theme.TA_MOVILTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TA_MOVILTheme {
                 // Se crea el NavBar
                Navigator()

            }
        }
    }
}

