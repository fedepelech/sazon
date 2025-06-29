package com.desarrolloaplicaciones.sazon

import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import com.desarrolloaplicaciones.sazon.data.Dificultad
import com.desarrolloaplicaciones.sazon.data.IngredientePost
import com.desarrolloaplicaciones.sazon.data.PasoPost
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RecetaPost
import com.desarrolloaplicaciones.sazon.data.RetrofitService
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TiposReceta
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas
import kotlinx.coroutines.CoroutineScope
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.desarrolloaplicaciones.sazon.data.Ingrediente
import com.desarrolloaplicaciones.sazon.data.TokenManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody


class CrearRecetaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Esto habilita edge to edge, si usas esta función
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            CrearRecetaScreen()
        }
    }
}
data class IngredienteInput(
    var nombre: String = "",
    var cantidad: String = "",
    var unidad: String = "gr"
)
data class PasoInput(
    var descripcion: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CrearRecetaScreen() {
    val context = LocalContext.current
    var categorias by remember { mutableStateOf<List<TiposReceta>>(emptyList()) }
    val dificultades= listOf("Facil", "Medio", "Dificil")
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var dificultadSeleccionada by remember { mutableStateOf("") }
    var ingredienteSeleccionado by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val unidades = listOf("ml", "gr", "un")
    var ingredientes by remember { mutableStateOf(listOf(IngredienteInput())) }
    var ingredientesDisponibles by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var pasos by remember { mutableStateOf(listOf(PasoInput())) }
    val imagenesSeleccionadas = remember { mutableStateListOf<Uri>() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imagenesSeleccionadas.clear()
        imagenesSeleccionadas.addAll(uris)
    }

    val scope = rememberCoroutineScope()
    val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }

    LaunchedEffect(true) {
        scope.launch {
            try {
                val categoriasResultado = retrofitService.obtenerCategorias()
                categorias = categoriasResultado;
                val ingredientesResultado = retrofitService.obtenerIngredientes()
                ingredientesDisponibles = ingredientesResultado
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->


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
                        "Subir Receta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD84F2A)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.93f)) {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Nombre de la receta") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.93f)) {
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripcion") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                var dificultadExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.93f)) {
                        ExposedDropdownMenuBox(
                            expanded = dificultadExpanded,
                            onExpandedChange = { dificultadExpanded = !dificultadExpanded }
                        ) {
                            OutlinedTextField(
                                value = dificultadSeleccionada,
                                onValueChange = { dificultadSeleccionada = it },
                                readOnly = true,
                                label = { Text("Dificultad") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dificultadExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = dificultadExpanded,
                                onDismissRequest = { dificultadExpanded = false }
                            ) {
                                dificultades.forEach { dificultad ->
                                    DropdownMenuItem(
                                        text = { Text(dificultad) },
                                        onClick = {
                                            dificultadSeleccionada = dificultad
                                            dificultadExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.93f)) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = categoriaSeleccionada,
                                onValueChange = { categoriaSeleccionada = it },
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categorias.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = { Text(categoria.nombre) },
                                        onClick = {
                                            categoriaSeleccionada = categoria.nombre
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                ingredientes.forEachIndexed { index, ingrediente ->
                    var ingredienteExpanded by remember(key1 = index) { mutableStateOf(false) }
                    var unidadExpanded by remember(key1 = index) { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dropdown de ingredientes disponibles
                        ExposedDropdownMenuBox(
                            expanded = ingredienteExpanded,
                            onExpandedChange = { ingredienteExpanded = !ingredienteExpanded }
                        ) {
                            OutlinedTextField(
                                value = ingrediente.nombre ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Ingrediente") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = ingredienteExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .width(160.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = ingredienteExpanded,
                                onDismissRequest = { ingredienteExpanded = false }
                            ) {
                                ingredientesDisponibles.forEach { nombre ->
                                    DropdownMenuItem(
                                        text = { Text(nombre.nombre) },
                                        onClick = {
                                            ingredientes = ingredientes.toMutableList().also {
                                                it[index] = it[index].copy(nombre = nombre.nombre)
                                            }
                                            ingredienteExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Campo de cantidad
                        OutlinedTextField(
                            value = ingrediente.cantidad.toString(),
                            onValueChange = { newValue ->
                                ingredientes = ingredientes.toMutableList().also {
                                    it[index] = it[index].copy(cantidad = newValue)
                                }
                            },
                            label = { Text("Cant") },
                            modifier = Modifier.width(80.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Dropdown de unidades
                        ExposedDropdownMenuBox(
                            expanded = unidadExpanded,
                            onExpandedChange = { unidadExpanded = !unidadExpanded }
                        ) {
                            OutlinedTextField(
                                value = ingrediente.unidad ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .width(90.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = unidadExpanded,
                                onDismissRequest = { unidadExpanded = false }
                            ) {
                                unidades.forEach { unidad ->
                                    DropdownMenuItem(
                                        text = { Text(unidad) },
                                        onClick = {
                                            ingredientes = ingredientes.toMutableList().also {
                                                it[index] = it[index].copy(unidad = unidad)
                                            }
                                            unidadExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (index == ingredientes.lastIndex) {
                            Column {
                                Button(
                                    onClick = {
                                        ingredientes = ingredientes + IngredienteInput()
                                    },
                                    modifier = Modifier.size(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+")
                                }

                                if (ingredientes.size > 1) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            ingredientes = ingredientes.dropLast(1)
                                        },
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84F2A))
                                    ) {
                                        Text("-", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                pasos.forEachIndexed { index, paso ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = paso.descripcion,
                                onValueChange = { newValue ->
                                    pasos = pasos.toMutableList().also {
                                        it[index] = it[index].copy(descripcion = newValue)
                                    }
                                },
                                label = { Text("Paso ${index + 1}") },
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            if (index == pasos.lastIndex) {
                                Column {
                                    Button(
                                        onClick = {
                                            pasos = pasos + PasoInput()
                                        },
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+")
                                    }

                                    if (pasos.size > 1) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = {
                                                pasos = pasos.dropLast(1)
                                            },
                                            modifier = Modifier.size(36.dp),
                                            contentPadding = PaddingValues(0.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84F2A))
                                        ) {
                                            Text("-", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.93f)) {
                        OutlinedTextField(
                            value = link,
                            onValueChange = { link = it },
                            label = { Text("Link al video") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box() {
                        Button(
                            modifier = Modifier.fillMaxWidth(0.93f),
                            onClick = { launcher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF409448))
                        ) {
                            Text("Subir Imagenes", color = Color.White)
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(imagenesSeleccionadas) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box() {
                        Button(
                            modifier = Modifier.fillMaxWidth(0.93f),
                            onClick = { guardarReceta(categorias,categoriaSeleccionada,titulo,descripcion, dificultadSeleccionada,ingredientes,pasos,retrofitService,scope, context, imagenes = imagenesSeleccionadas) },
                            //onClick = { guardarReceta(categorias,categoriaSeleccionada,titulo,descripcion, dificultadSeleccionada,ingredientes,pasos,retrofitService,scope) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF409448))
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    }
                }
            }


        }
    }
}

fun crearParteImagen(uri: Uri, context: Context): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val fileBytes = inputStream.readBytes()
    val requestFile = RequestBody.create(
        context.contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "image/*".toMediaType(),
        fileBytes
    )
    val fileName = "imagen_${System.currentTimeMillis()}.jpg"
    return MultipartBody.Part.createFormData("imagen", fileName, requestFile)
}


/*fun guardarReceta(
    categorias: List<TiposReceta>,
    categoriaSeleccionada: String,
    titulo: String,
    descripcion: String,
    dificultadSeleccionada: String,
    ingredientes: List<IngredienteInput>,
    pasos: List<PasoInput>,
    retrofitService: RetrofitService,
    scope: CoroutineScope
) {
    val categoria = categorias.find { it.nombre == categoriaSeleccionada }

    if (categoria != null) {
        val recetaPost = RecetaPost(
            nombre = titulo,
            tipo_id = categoria.id,
            descripcion = descripcion,
            dificultad = Dificultad(dificultadSeleccionada),
            ingredientes = ingredientes.mapNotNull { ing ->
                val cantidadInt = ing.cantidad.toInt()
                if (cantidadInt != null && ing.nombre.isNotBlank() && ing.unidad.isNotBlank()) {
                    IngredientePost(
                        nombre = ing.nombre,
                        cantidad = cantidadInt,
                        unidad = ing.unidad
                    )
                } else null
            },
            pasos = pasos.mapIndexedNotNull { index, paso ->
                if (paso.descripcion.isNotBlank()) {
                    PasoPost(
                        paso_numero = index + 1,
                        descripcion = paso.descripcion
                    )
                } else null
            }
        )
        println(recetaPost)

        scope.launch {
            try {
                val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJEQjNENzE0Qi05RTcyLTQxOTYtQTEyMC05RTdFQ0U1ODg0MTciLCJpYXQiOjE3NTEyMTY0NTQsImV4cCI6MTc1MTMwMjg1NH0.Gj-oLJlFfOoyxX3kW1hH0DP4vHrR2mbB-lGfX4e7Fbc"
                val response = retrofitService.subirReceta("Bearer $accessToken", recetaPost)
                println(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}*/

fun guardarReceta(
    categorias: List<TiposReceta>,
    categoriaSeleccionada: String,
    titulo: String,
    descripcion: String,
    dificultadSeleccionada: String,
    ingredientes: List<IngredienteInput>,
    pasos: List<PasoInput>,
    retrofitService: RetrofitService,
    scope: CoroutineScope,
    context: Context,
    imagenes: List<Uri>
) {
    val categoria = categorias.find { it.nombre == categoriaSeleccionada }
    if (categoria != null) {
        val recetaPost = RecetaPost(
            nombre = titulo,
            tipo_id = categoria.id,
            descripcion = descripcion,
            dificultad = Dificultad(dificultadSeleccionada),
            ingredientes = ingredientes.mapNotNull { ing ->
                val cantidadInt = ing.cantidad.toIntOrNull()
                if (cantidadInt != null && ing.nombre.isNotBlank() && ing.unidad.isNotBlank()) {
                    IngredientePost(ing.nombre, cantidadInt, ing.unidad)
                } else null
            },
            pasos = pasos.mapIndexedNotNull { index, paso ->
                if (paso.descripcion.isNotBlank()) {
                    PasoPost(index + 1, paso.descripcion)
                } else null
            }
        )

        scope.launch {
            try {
                val token = TokenManager.getAccessToken()
                val response = retrofitService.subirReceta("Bearer $token", recetaPost)

                if (response.isSuccessful) {
                    val recetaResponse = response.body()
                    val recetaId = recetaResponse?.id

                    if (recetaId != null) {
                        imagenes.forEach { uri ->
                            val part = crearParteImagen(uri, context)
                            val imagenResponse =
                                retrofitService.subirImagenReceta(recetaId, part, "Bearer $token")
                            println("Imagen subida: ${imagenResponse.code()}")
                        }

                        // Show success Toast and redirect to MisRecetasActivity
                        Toast.makeText(context, "Receta guardada con éxito", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(context, MisRecetasActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Error al obtener el ID de la receta",
                            Toast.LENGTH_LONG
                        ).show()
                        println("No se pudo obtener el ID de la receta")
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error al subir receta: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                    println("Error al subir receta: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CrearRecetaPreview() {
    CrearRecetaScreen()
}