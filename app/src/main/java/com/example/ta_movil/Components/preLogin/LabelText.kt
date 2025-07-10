package com.example.ta_movil.Components.preLogin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LabelText(string: String){
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        text = string,
        color = Color(0xFF513C31),
        textAlign = TextAlign.Start
    )
}