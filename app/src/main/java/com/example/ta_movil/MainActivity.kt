package com.example.ta_movil

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ta_movil.Components.MainNavigation
import com.example.ta_movil.ui.theme.TA_MOVILTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private var isFirebaseInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar Google Play Services
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        
        if (status != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play Services no está disponible", Toast.LENGTH_LONG).show()
            return
        }
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        isFirebaseInitialized = true
        
        enableEdgeToEdge()
        setContent {
            TA_MOVILTheme {
                MainNavigation(auth)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        
        if (!isFirebaseInitialized) {
            Toast.makeText(this, "Firebase no inicializado correctamente", Toast.LENGTH_LONG).show()
            return
        }
        
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // El usuario está logueado
        } else {
            // El usuario no está logueado
        }
    }
}
