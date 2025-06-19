package com.example.ta_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ta_movil.Components.Navigator
import com.example.ta_movil.ui.theme.TA_MOVILTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth // Lateinit indica que después se inicializará.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth // Se inicializa la variable auth.
        enableEdgeToEdge()
        setContent {
            TA_MOVILTheme {
                 // Se crea el NavBar
                Navigator(auth)

            }
        }
    }

    // Se llama luego del onCreate
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser // Esto indica si es que está logueado o no.
        if (currentUser != null) {
            // El usuario está logueado
        } else {
            // El usuario no está logueado
        }


    }
}

