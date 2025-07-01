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
import com.desarrolloaplicaciones.sazon.data.RecentRecipeReturn
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RetrofitService
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas2

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
    //var recetas by remember { mutableStateOf<List<RecentRecipeReturn>>(emptyList()) }
    var recetas by remember { mutableStateOf<List<RecetaConImagen>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }
    val usuario_id = TokenManager.getUserId().toString();
    
    LaunchedEffect(true) {
        scope.launch {
            try {
                val recetasbase = retrofitService.getRecetasPorUsuario(usuario_id).sortedBy { it.nombre }
                recetas = completarImagenesRecetas2(retrofitService, recetasbase)
                loading = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Scaffold(bottomBar = { BottomNavigationBar() }) { innerPadding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFDF5ED))
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD84F2A))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFDF5ED))
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 600.dp) // Limita el ancho de la pantalla al igual que HomeActivity
                        .fillMaxHeight()
                        .background(Color(0xFFFDF5ED))
                        .padding(horizontal = 16.dp) // Agrega relleno lateral para centrar el contenido
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

                    if (recetas.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tenés recetas creadas",
                                    fontSize = 18.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        items(recetas) { receta ->
                            RecipeCard(receta)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar() {
    val context = LocalContext.current
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

            // Botón Perfil
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

//@Composable
//fun RecetaCard(
//    titulo: String,
//    resenias: Int,
//    estrellas: Int,
//    ingredientes: List<String>,
//    imagenUrl: String?,
//    recetaId: String
//) {
//    val token = TokenManager.getAccessToken()
//    val painter = rememberAsyncImagePainter(
//        ImageRequest.Builder(LocalContext.current)
//            .data("$imagenUrl")
//            .addHeader("Authorization", "Bearer $token")
//            .build()
//    )
//    val context = LocalContext.current
//    Row(
//        modifier = Modifier
//            .clickable {
//                val intent = Intent(context, ProductPageActivity::class.java).apply {
//                    putExtra("recetaId", recetaId)
//                }
//                context.startActivity(intent)
//            }
//            .fillMaxWidth()
//            .background(Color(0xFFFDF5ED))
//            .drawBehind {
//                val strokeWidth = 1.dp.toPx()
//                val y = size.height - strokeWidth / 2
//                drawLine(
//                    color = Color(0xFF4CAF50),
//                    start = Offset(0f, y),
//                    end = Offset(size.width, y),
//                    strokeWidth = strokeWidth
//                )
//            }
//            .padding(12.dp)
//    ) {
//        if (imagenUrl != null) {
//            Image(
//                painter = rememberAsyncImagePainter("$imagenUrl"),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )
//        } else {
//            Image(
//                painter = painterResource(id = R.drawable.logo2),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = titulo,
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF388E3C)
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun MisRecetasPreview() {
    MisRecetasScreen()
}