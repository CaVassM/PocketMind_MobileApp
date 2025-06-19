package com.example.ta_movil.Views.userLogin

import androidx.compose.foundation.Image
import com.example.ta_movil.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ta_movil.Additionals.Dimens
import com.example.ta_movil.Components.preLogin.ButtonApp
import com.example.ta_movil.Model.pagesInit.pageModel
import com.example.ta_movil.ui.theme.Nunito

@Composable
fun Home(onNext: () -> Unit) {

    // Este estado mantiene la página actual del carrusel
    // Claro cuando aprietas un boton, o deslizas, el Composable va a reaccionar.
    // Su primera instancia seria destruir el Composable con el evento lanzado (parecido a React)
    // Pero remember se encarga de conservar esa parte del UI
    // Por eso declaramos pagerState, un controlador que permite capturar un state de la pagina que se encuentra.
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Aquí defines las imágenes y contenido para cada página
    val pages = listOf(
        pageModel(
            image = R.drawable.piggy_2, // Primera imagen
            title = "Empieza hoy el camino hacia tus sueños",
            description = "El futuro empieza con una idea... y una moneda"
        ),
        pageModel(
            image = R.drawable.piggy_1, // Segunda imagen
            title = "Invierte Sabiamente",
            description = "Haz crecer tu dinero con inversiones seguras"
        ),
        pageModel(
            image = R.drawable.logo, // Tercera imagen
            title = "Controla tu Presupuesto",
            description = "Mantén un control total de tus gastos"
        )
    )
    // No se usa un topBar ni bottomBar evidentemente.
    // No se usara lazyColumn
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDEB887))
            .padding(
                horizontal = Dimens.paddingLarge,
                vertical = Dimens.paddingSmall
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        // Para lograr un carrusel de imagenes, usar HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) {page ->
            PageContent(
                page = pages[page],
            )
        }
        Spacer(modifier = Modifier.size(85.dp))
        PageIndicators(
            pageCount = pages.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.size(85.dp))
        // Mandamos la funcion callback onNext
        ButtonApp(onNext, "Ahorra ya", true) // True pues, no cuenta con ningun requerimiento

    }

}

// Esta función crea los indicadores de página (las bolitas)
@Composable
fun PageIndicators(
    pageCount: Int, // Cuántas páginas tenemos en total
    currentPage: Int, // Cuál es la página actual
    modifier: Modifier = Modifier
) {
    // Usamos Row para alinear los círculos horizontalmente
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall), // Espacio entre cada círculo
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Creamos un indicador para cada página
        // repeat es como un for loop que se ejecuta pageCount veces
        // Renderizamos entonces, un total de 3 bolias
        repeat(pageCount) { index ->
            Circle(
                isSelected = index == currentPage // Es este el indicador de la página actual?.
            )
        }
    }
}

// Esta función crea un solo indicador (un círculo)
@Composable
fun Circle(
    isSelected: Boolean, // Este indicador representa la página actual?
) {
    Box(
        modifier = Modifier
            .size(12.dp) // Tamaño del círculo
            .clip(CircleShape) // Hace que el Box tenga forma circular
            .background(
                color = if (isSelected) {
                    Color.Black // Círculo lleno y oscuro para la página actual
                } else {
                    Color.Black.copy(alpha = 0.3f) // Círculo semi-transparente para las otras páginas
                }
            )
    )
}

@Composable
fun PageContent(page: pageModel) {
    // Aqui tenemos un Page, procedemos con emitirlo.
    // Haremos scroll a columnas.
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente

    ){
        Image(
            painter = painterResource(page.image),
            contentDescription = "none",
            modifier = Modifier
                .size(
                    Dimens.imageSize
                ),
            contentScale = ContentScale.Fit
        )
        // Titulo
        Text(
            text = page.title,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF513C31)

        )
        // Descripcion
        Spacer(modifier = Modifier.size(32.dp))
        Text(
            text = page.description,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF513C31)
        )
    }

}
