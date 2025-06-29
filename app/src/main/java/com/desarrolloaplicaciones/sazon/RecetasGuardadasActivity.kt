package com.desarrolloaplicaciones.sazon


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.*
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas

class RecetasGuardadasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto habilita edge to edge, si usas esta función
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            RecetasGuardadasScreen()
        }
    }
}


@Composable
fun RecetasGuardadasScreen() {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        //var recetas by remember { mutableStateOf<List<RecetaModel>>(emptyList()) }
        val scope = rememberCoroutineScope()
        var recetas by remember { mutableStateOf<List<RecetaConImagen>>(emptyList()) }
        var cargando by remember { mutableStateOf(false) }
        val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }
        val token = TokenManager.getAccessToken()

        LaunchedEffect(true) {
            scope.launch {
                try {
                    /*val recetasbase = retrofitService.getRecentRecipes().sortedBy { it.nombre }
                    recetas = completarImagenesRecetas(retrofitService, recetasbase)*/
                    cargando = true
                    val recetasbase =
                        retrofitService.getRecetasGuardadas("Bearer $token").sortedBy { it.nombre }
                    recetas = completarImagenesRecetas(retrofitService, recetasbase)
                    cargando = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    cargando = false
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
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
                            "Recetas Guardadas",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD84F2A)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                if (recetas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tenés recetas guardadas",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
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

            if (cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFD84F2A))
                }
            }
        }
    }
}
