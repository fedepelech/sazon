package com.desarrolloaplicaciones.sazon


import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas3

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
private fun BottomNavigationBar() {
    val context = LocalContext.current

    // Verificar si el usuario está autenticado
    val isAuthenticated = !TokenManager.getAccessToken().isNullOrEmpty()

    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Gray,
        modifier = Modifier.height(80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isAuthenticated) {
                Arrangement.SpaceEvenly
            } else {
                Arrangement.SpaceAround
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Home
            IconButton(
                onClick = {
                    // Navegar a Home si no estamos ya aquí
                    if (context !is HomeActivity) {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color(0xFF409448)
                )
            }

            // Botón flotante central - SOLO mostrar si está autenticado
            if (isAuthenticated) {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, CrearRecetaActivity::class.java)
                        context.startActivity(intent)
                    },
                    containerColor = Color(0xFF409448),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar receta"
                    )
                }
            }

            // Botón Perfil (activo en esta pantalla)
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAuthenticated) {
                    IconButton(onClick = {
                        val intent = Intent(context, ProfileActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color(0xFF409448)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = "Iniciar sesión",
                            tint = Color(0xFF409448)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecetasGuardadasScreen() {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()
        var recetas by remember { mutableStateOf<List<RecetaConImagen>>(emptyList()) }
        var cargando by remember { mutableStateOf(false) }
        val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }
        val token = TokenManager.getAccessToken()

        LaunchedEffect(true) {
            scope.launch {
                try {
                    cargando = true
                    val recetasbase =
                        retrofitService.getRecetasGuardadas("Bearer $token").body()
                    recetas = recetasbase?.let { completarImagenesRecetas3(retrofitService, it) }!!
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp) // Ajuste igual que MisRecetasActivity
                        ) {
                            RecipeCard(receta)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
