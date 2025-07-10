package com.example.ta_movil.Components.preLogin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.ui.theme.Nunito

@Composable
fun ClickableText(text: String, onClick: () -> Unit){
    Text(
        text = text,
        fontFamily = Nunito,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF513C31),
        modifier = Modifier
            .clickable(onClick = onClick) // Esto hace que sea clickable
    )
}