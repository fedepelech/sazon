package com.desarrolloaplicaciones.sazon


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip




@Composable
fun ProfileViewActivity() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            HeaderProfileView("Lucas", "Castro")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recetas de Lucas", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84F2A))
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
fun HeaderProfileView(name: String, username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD3CDC6))
            .height(200.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .padding(12.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("$name $username", fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    ProfileViewActivity()
}