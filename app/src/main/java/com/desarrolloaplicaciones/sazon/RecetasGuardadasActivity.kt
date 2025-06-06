package com.desarrolloaplicaciones.sazon


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview




@Composable
fun RecetasGuardadasActivity() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            SazonHeader()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recetas Guardadas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84F2A))
            Spacer(modifier = Modifier.height(24.dp))
        }
        Column {
            RecetaCard(
                titulo = "Fideos con crema y jamón",
                resenias = 11,
                estrellas = 3,
                ingredientes = listOf("Fideos", "crema para cocinar", "jamón", "manteca", "queso rallado"),
                imagenId = R.drawable.logo2
            )
            RecetaCard(
                titulo = "Fideos con crema y jamón",
                resenias = 11,
                estrellas = 3,
                ingredientes = listOf("Fideos", "crema para cocinar", "jamón", "manteca", "queso rallado"),
                imagenId = R.drawable.logo2
            )
            RecetaCard(
                titulo = "Fideos con crema y jamón",
                resenias = 11,
                estrellas = 3,
                ingredientes = listOf("Fideos", "crema para cocinar", "jamón", "manteca", "queso rallado"),
                imagenId = R.drawable.logo2
            )
            RecetaCard(
                titulo = "Fideos con crema y jamón",
                resenias = 11,
                estrellas = 3,
                ingredientes = listOf("Fideos", "crema para cocinar", "jamón", "manteca", "queso rallado"),
                imagenId = R.drawable.logo2
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            BottomNavigationBar()
        }
    }


}

@Preview(showBackground = true)
@Composable
fun RecetasGuardadasPreview() {
    RecetasGuardadasActivity()
}