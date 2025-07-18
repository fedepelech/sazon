package com.desarrolloaplicaciones.sazon


import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RetrofitService
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas3
import kotlinx.coroutines.CoroutineScope

class ChangeImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            ChangeImageScreen()
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
fun ChangeImageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            SazonHeader()
        }

        Column {
            ChangeImageBodySection()
            Spacer(modifier = Modifier.height(32.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            BottomNavigationBar()
        }
    }
}

@Composable
fun ChangeImageBodySection() {
    val context = LocalContext.current
    val activity = context as? Activity
    var imagen by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }
    val user_id = TokenManager.getUserId()
    val imagenesSeleccionadas = remember { mutableStateListOf<Uri>() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenesSeleccionadas.clear()
        uri?.let { imagenesSeleccionadas.add(it) }
    }


    LaunchedEffect(true) {
        scope.launch {
            try {

                val usuario = user_id?.let { retrofitService.obtenerUsuario(it) }
                usuario?.imagenesPerfil?.imagenes?.lastOrNull()?.let {
                    imagen = it.url
                    println(imagen)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(Color(0xFFFDF5ED))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(16.dp))
            Text("Cambiar Imagen de Perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3A9440))
            Spacer(modifier = Modifier.height(24.dp))

            if (imagen.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de perfil por defecto",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape),
                    tint = Color.Gray
                )
            } else {
                AsyncImage(
                    model = imagen,
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A9440))
            ) {
                Text("Seleccionar Imagen", color = Color.White, modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                if (imagenesSeleccionadas.isNotEmpty()) {
                    Text(
                        text = "Imagen Seleccionada",
                        color = Color.Gray
                    )
                    Button(
                        onClick = {
                            if (user_id != null) {
                                cargarImagen(retrofitService,scope, context, imagenes = imagenesSeleccionadas,user_id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A9440))
                    ) {
                        Text("Actualizar Imagen", color = Color.White, modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center)
                    }
                } else {
                    Text(
                        text = "No se seleccionó imagen",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


fun cargarImagen(
    retrofitService: RetrofitService,
    scope: CoroutineScope,
    context: Context,
    imagenes: List<Uri>,
    usuarioId: String
){
    scope.launch{
        try {
            val token = TokenManager.getAccessToken()
            imagenes.forEach { uri ->
                val part = crearParteImagen(uri, context)
                val imagenResponse =
                    retrofitService.subirImagenUsuario(usuarioId, part, "Bearer $token")
                println("Imagen subida: ${imagenResponse.code()}")
            }
            Toast.makeText(context, "Imagen de perfil actualizada con exito", Toast.LENGTH_LONG)
                .show()
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        catch (e: Exception){
            Toast.makeText(
                context,
                "Error al subir la imagen",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }
}




@Preview(showBackground = true)
@Composable
fun CHangeImageScreenPreview() {
    MaterialTheme {
        ProfileEditScreen()
    }
}