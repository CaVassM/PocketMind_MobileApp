package com.example.ta_movil.Components.preLogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ta_movil.R

@Composable
fun IconGoogle(){
    Box(
        modifier = Modifier
            .size(width = 167.dp, height = 30.dp)
            .background(
                color = Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ){
        // Linea
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 1.dp,
            color = Color(0xFF4E3B2F)
        )
        // Ahora el icon.
        IconButton(
            onClick = { /* Pendiente para que se conecte con Google */ },
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(Color.White)

        ) {
            Image(
                painter = painterResource(R.drawable.google_icon),
                contentDescription = "Google"
            )
        }
    }
}