package com.desarrolloaplicaciones.sazon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset



@Composable
fun MisRecetasActivity() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            SazonHeader()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mis Recetas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84F2A))
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


@Composable
fun RecetaCard(
    titulo: String,
    resenias: Int,
    estrellas: Int,
    ingredientes: List<String>,
    imagenId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDF5ED))
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Color(0xFF4CAF50),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = imagenId),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 📝 Información
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF388E3C) // Verde fuerte
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "($resenias reseñas)",
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                /*repeat(estrellas) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrella",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }*/
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ingredientes:",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD84F2A)
            )
            Text(
                text = ingredientes.joinToString(", "),
                color = Color(0xFFFFA000),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisRecetasPreview() {
    MisRecetasActivity()
}