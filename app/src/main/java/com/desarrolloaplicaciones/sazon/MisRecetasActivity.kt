package com.desarrolloaplicaciones.sazon
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.*
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext

class MisRecetasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto habilita edge to edge, si usas esta función
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            MisRecetasScreen()
        }
    }
}


@Composable
fun MisRecetasScreen() {
    var recetas by remember { mutableStateOf<List<RecetaModel>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch {
            try {
                recetas = RetrofitClient.api.obtenerRecetas()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Scaffold (bottomBar = { BottomNavigationBar() }) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF5ED))
                .padding(innerPadding)
        ) {
            // Encabezado
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SazonHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Mis Recetas",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD84F2A)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Lista de recetas
            items(recetas) { receta ->
                RecetaCard(
                    titulo = receta.nombre,
                    resenias = 11,
                    estrellas = 3,
                    ingredientes = listOf("Ingrediente 1", "Ingrediente 2"),
                    imagenId = R.drawable.logo2
                )
            }

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
    val context = LocalContext.current;
    Row(
        modifier = Modifier
            .clickable {
                context.startActivity(Intent(context, ProfileActivity::class.java))
            }
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
    MisRecetasScreen()
}