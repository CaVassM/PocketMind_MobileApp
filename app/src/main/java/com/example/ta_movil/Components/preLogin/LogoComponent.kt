package com.example.ta_movil.Components.preLogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ta_movil.Additionals.Dimens
import com.example.ta_movil.R

@Composable
fun LogoComponent(){
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "logo",
        modifier = Modifier
            .size(
                Dimens.imageSize
            ),
        contentScale = ContentScale.Fit
    )
}