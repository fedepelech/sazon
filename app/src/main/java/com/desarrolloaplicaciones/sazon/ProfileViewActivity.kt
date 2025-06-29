package com.desarrolloaplicaciones.sazon


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.*
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas

class ProfileViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto habilita edge to edge, si usas esta función
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            ProfileViewScreen()
        }
    }
}




@Composable
fun ProfileViewScreen() {
    //var recetas by remember { mutableStateOf<List<RecetaModel>>(emptyList()) }
    var recetas by remember { mutableStateOf<List<RecetaConImagen>>(emptyList()) }
    val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch {
            try {
                val recetasbase = retrofitService.getRecentRecipes().sortedBy { it.nombre }
                recetas = completarImagenesRecetas(retrofitService, recetasbase)
                /*val recetasbase = retrofitService.getRecetasPorUsuario(usuario_id).sortedBy { it.nombre }
                recetas = completarImagenesRecetas(retrofitService, recetasbase)*/

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold (bottomBar = { BottomNavigationBar() } ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF5ED))
                .padding(innerPadding)
                    //verticalArrangement = Arrangement.SpaceBetween
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HeaderProfileView("Lucas", "Castro")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Recetas de Lucas",
                        fontSize = 30.sp,
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
                    imagenUrl = receta.imagenUrl,
                    recetaId = receta.id
                )
            }

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
    ProfileViewScreen()
}