package com.desarrolloaplicaciones.sazon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip

// Clases de datos para la app - Modelos existentes
data class Comentario(
    val nombre: String,
    val texto: String,
    val tiempo: String
)

data class Receta(
    val nombre: String,
    val tipo_id: Int,
    val dificultad_id: Int,
    val descripcion: String,
    val ingredientes: List<Ingrediente>,
    val pasos: List<Paso>
)

data class Paso(
    val paso_numero: Int,
    val descripcion: String
)

data class Ingrediente(
    val nombre: String,
    val cantidad: Int,
    val unidad: String
)

// Nuevos modelos para la API
data class RecetaDetalle(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val fecha_creacion: String,
    val tipo: String,
    val tipo_id: Int,
    val dificultad: String,
    val dificultad_id: Int,
    val autor: String,
    val autor_id: String,
    val ingredientes: List<IngredienteDetalle>,
    val pasos: List<PasoDetalle>
)

data class IngredienteDetalle(
    val id: String,
    val nombre: String,
    val cantidad: Int,
    val unidad: String
)

data class PasoDetalle(
    val id: String,
    val paso_numero: Int,
    val descripcion: String
)

// Nuevos modelos para comentarios
data class ComentarioResponse(
    val receta: RecetaInfo,
    val estadisticas: EstadisticasComentarios,
    val distribucion_valoraciones: List<DistribucionValoracion>,
    val comentarios: List<ComentarioModel>
)

data class RecetaInfo(
    val id: String,
    val nombre: String
)

data class EstadisticasComentarios(
    val total_comentarios: Int,
    val total_valoraciones: Int,
    val promedio_valoracion: Double,
    val valoracion_minima: Int,
    val valoracion_maxima: Int
)

data class DistribucionValoracion(
    val estrellas: Int,
    val cantidad: Int
)

data class ComentarioModel(
    val id: String,
    val texto: String,
    val valoracion: Int,
    val fecha: String,
    val autor: String,
    val autor_id: String
)

// Modelo para enviar comentarios
data class ComentarioRequest(
    val recetaId: String,
    val texto: String,
    val valoracion: Int
)

// Modelos para imágenes
data class ImagenRecetaResponse(
    val receta_id: String,
    val receta_titulo: String,
    val imagenes: List<ImagenDetalle>,
    val total: Int
)

data class ImagenDetalle(
    val id: String,
    val url: String,
    val nombre_archivo: String,
    val orden: Int,
    val es_principal: Boolean,
    val fecha_subida: String
)

// Clase principal de la actividad
class ProductPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el ID de la receta del Intent
        val recetaId = intent.getStringExtra("RECETA_ID") ?: "641F8DB6-89CA-45F8-BA3B-5CC01A4A9DBE"

        setContent {
            ProductPageContent(recetaId = recetaId)
        }
    }
}

@Composable
fun ProductPageContent(recetaId: String) {
    var receta by remember { mutableStateOf<RecetaDetalle?>(null) }
    var comentarioResponse by remember { mutableStateOf<ComentarioResponse?>(null) }
    var imagenPrincipal by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var reloadTrigger by remember { mutableStateOf(0) }

    // Función para recargar comentarios
    val recargarComentarios: () -> Unit = {
        reloadTrigger++
    }

    // Cargar la receta, imágenes y comentarios cuando se inicializa o cuando se solicita recarga
    LaunchedEffect(recetaId, reloadTrigger) {
        try {
            isLoading = true
            error = null

            if (reloadTrigger == 0) {
                // Primera carga: cargar receta
                receta = RetrofitClient.api.obtenerRecetaPorId(recetaId)

                // Cargar imágenes y obtener la principal
                try {
                    val imagenesResponse = RetrofitClient.api.obtenerImagenReceta(recetaId)
                    imagenPrincipal = imagenesResponse.imagenes
                        .firstOrNull { it.es_principal }?.url
                        ?: imagenesResponse.imagenes.firstOrNull()?.url
                } catch (e: Exception) {
                    // Si no se pueden cargar las imágenes, continuar sin ellas
                    imagenPrincipal = null
                }
            }

            // Siempre recargar comentarios (para actualizaciones)
            comentarioResponse = RetrofitClient.api.obtenerComentariosPorReceta(recetaId)

        } catch (e: Exception) {
            error = "Error al cargar datos: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading && reloadTrigger == 0 -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFDF6EB)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF409448))
            }
        }
        error != null && receta == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFDF6EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        receta != null -> {
            val scrollState = rememberScrollState()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFDF6EB))
                    .verticalScroll(scrollState)
            ) {
                ImagenSazon()
                RecetaTitulo(nombre = receta!!.nombre)
                RecetaUtility(receta = receta, estadisticas = comentarioResponse?.estadisticas)
                ImagenProducto(imagenUrl = imagenPrincipal)
                ListaIngredientesAPI(ingredientes = receta!!.ingredientes)
                ListaPasosAPI(pasos = receta!!.pasos)
                VideoReceta(videoUrl = "https://www.youtube.com/watch?v=ejemplo")
                CalculadoraIngredientesAPI(ingredientes = receta!!.ingredientes)
                AgregarComentario(
                    recetaId = recetaId,
                    onComentarioAgregado = recargarComentarios
                )
                ListaComentariosAPI(comentarioResponse = comentarioResponse)
            }
        }
    }
}

@Composable
fun ImagenSazon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Logo",
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
fun RecetaTitulo(nombre: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = nombre,
            color = Color(0xFF409448),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun RecetaUtility(receta: RecetaDetalle? = null, estadisticas: EstadisticasComentarios? = null) {
    var isFavorite by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // BOTON FAVORITO
        IconButton(
            onClick = {
                isFavorite = !isFavorite
            }
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                tint = if (isFavorite) Color(0xFF409448) else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        // CALIFIACION PROMEDIO
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = estadisticas?.let { String.format("%.1f", it.promedio_valoracion) } ?: "Sin calificar",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "(${estadisticas?.total_valoraciones ?: 0})",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Nombre del autor
        Text(
            text = "Por: ${receta?.autor ?: "Usuario desconocido"}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ImagenProducto(imagenUrl: String? = null) {
    if (imagenUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imagenUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Imagen de la receta",
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.logo2),
            error = painterResource(id = R.drawable.logo2)
        )
    }
}

// Nuevos componentes para API
@Composable
fun ListaIngredientesAPI(ingredientes: List<IngredienteDetalle>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Ingredientes:",
            color = Color(0xFF409448),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 1.dp)
                .align(Alignment.Start)
        )
        ingredientes.forEach { ingrediente ->
            Text(
                text = "。 ${ingrediente.cantidad} ${ingrediente.unidad} de ${ingrediente.nombre}",
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ListaPasosAPI(pasos: List<PasoDetalle>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Pasos:",
            color = Color(0xFF409448),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 1.dp)
                .align(Alignment.Start)
        )
        pasos.forEach { paso ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Paso ${paso.paso_numero}:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF409448)
                )
                Text(
                    text = paso.descripcion,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VideoReceta(videoUrl: String = "https://www.youtube.com/watch?v=ejemplo") {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Abre el enlace de YouTube
                val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
                context.startActivity(intent)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_youtube),
            contentDescription = "YouTube",
            tint = Color(0xFFFF0000),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Video receta",
            color = Color(0xFFFF0000),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun CalculadoraIngredientes(
    ingredientes: List<Ingrediente>,
    porcionesOriginales: Int = 1
) {
    var showDialog by remember { mutableStateOf(false) }
    var factorMultiplicador by remember { mutableStateOf(1f) }
    var porcionesDeseadas by remember { mutableStateOf(porcionesOriginales.toString()) }
    var ingredienteSeleccionado by remember { mutableStateOf<Ingrediente?>(null) }
    var cantidadAjustada by remember { mutableStateOf("") }
    var modoCalculo by remember { mutableStateOf(0) }

    // Botón que activa el diálogo
    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF409448)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = "Calcular",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Calcular Ingredientes",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    // Diálogo para ajustar ingredientes
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ajustar cantidades") },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        "Método de cálculo:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val opciones = listOf("Multiplicador", "Porciones", "Por ingrediente")
                    opciones.forEachIndexed { index, opcion ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { modoCalculo = index }
                        ) {
                            RadioButton(
                                selected = modoCalculo == index,
                                onClick = { modoCalculo = index }
                            )
                            Text(opcion)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Según el modo seleccionado, mostrar diferentes controles
                    when (modoCalculo) {
                        0 -> {
                            Text("Factor multiplicador:")
                            OutlinedTextField(
                                value = factorMultiplicador.toString(),
                                onValueChange = { newValue ->
                                    factorMultiplicador = newValue.toFloatOrNull() ?: 1f
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        1 -> {
                            Text("Porciones deseadas:")
                            OutlinedTextField(
                                value = porcionesDeseadas,
                                onValueChange = { porcionesDeseadas = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        2 -> {
                            Text("Seleccionar ingrediente:")
                            LazyColumn(modifier = Modifier.height(100.dp)) {
                                items(ingredientes) { ingrediente ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { ingredienteSeleccionado = ingrediente }
                                            .padding(4.dp)
                                    ) {
                                        RadioButton(
                                            selected = ingredienteSeleccionado == ingrediente,
                                            onClick = { ingredienteSeleccionado = ingrediente }
                                        )
                                        Text("${ingrediente.cantidad} ${ingrediente.unidad} de ${ingrediente.nombre}")
                                    }
                                }
                            }

                            ingredienteSeleccionado?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Nueva cantidad para ${it.nombre}:")
                                OutlinedTextField(
                                    value = cantidadAjustada,
                                    onValueChange = { cantidadAjustada = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Muestra los ingredientes ajustados
                    Text(
                        "Ingredientes ajustados:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Calcula y muestra los ingredientes ajustados
                    val factor = when (modoCalculo) {
                        0 -> factorMultiplicador
                        1 -> if (porcionesDeseadas.isNotEmpty())
                            porcionesDeseadas.toFloatOrNull() ?: 1f
                        else 1f
                        2 -> {
                            if (ingredienteSeleccionado != null && cantidadAjustada.isNotEmpty()) {
                                val nuevaCantidad = cantidadAjustada.toFloatOrNull() ?: 1f
                                nuevaCantidad / ingredienteSeleccionado!!.cantidad
                            } else 1f
                        }
                        else -> 1f
                    }

                    ingredientes.forEach { ingrediente ->
                        val nuevaCantidad = (ingrediente.cantidad * factor).toInt()
                        Text("• $nuevaCantidad ${ingrediente.unidad} de ${ingrediente.nombre}")
                    }

                    if (modoCalculo == 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Factor calculado: ${String.format("%.2f", factor)}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CalculadoraIngredientesAPI(
    ingredientes: List<IngredienteDetalle>,
    porcionesOriginales: Int = 1
) {
    // Convertir IngredienteDetalle a Ingrediente para reutilizar el componente existente
    val ingredientesConvertidos = ingredientes.map { ing ->
        Ingrediente(
            nombre = ing.nombre,
            cantidad = ing.cantidad,
            unidad = ing.unidad
        )
    }

    CalculadoraIngredientes(ingredientes = ingredientesConvertidos, porcionesOriginales)
}

@Composable
fun AgregarComentario(recetaId: String, onComentarioAgregado: () -> Unit) {
    var texto by remember { mutableStateOf("") }
    var valoracion by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Función para enviar comentario
    fun enviarComentario() {
        if (texto.trim().isEmpty()) {
            error = "Por favor ingresa un comentario"
            return
        }
        if (valoracion == 0) {
            error = "Por favor selecciona una calificación"
            return
        }

        isLoading = true
        error = null
        successMessage = null

        scope.launch {
            try {
                val comentarioRequest = ComentarioRequest(
                    recetaId = recetaId,
                    texto = texto.trim(),
                    valoracion = valoracion
                )

                RetrofitClient.api.crearComentario(comentarioRequest)

                // Limpiar formulario y mostrar mensaje de éxito
                texto = ""
                valoracion = 0
                successMessage = "¡Comentario enviado exitosamente!"

                // Recargar comentarios
                onComentarioAgregado()

                // Ocultar mensaje después de 3 segundos
                kotlinx.coroutines.delay(3000)
                successMessage = null

            } catch (e: Exception) {
                error = "Error al enviar comentario: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Burbuja para agregar comentario
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Agregar comentario",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF409448),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Selector de valoración con estrellas
            Text(
                text = "Valoración:",
                fontWeight = FontWeight.Medium,
                color = Color(0xFF409448),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { valoracion = index + 1 },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrella ${index + 1}",
                            tint = if (index < valoracion) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (valoracion > 0) {
                    Text(
                        text = "$valoracion/5",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF409448),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                }
            }

            // Campo de texto para el comentario
            OutlinedTextField(
                value = texto,
                onValueChange = {
                    texto = it
                    error = null
                    successMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Escribe tu comentario aquí...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF409448),
                    unfocusedBorderColor = Color(0xFFBDBDBD)
                ),
                enabled = !isLoading
            )

            // Mostrar mensajes de error o éxito
            error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            successMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFF409448),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { enviarComentario() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF409448)
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Enviar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ListaComentariosAPI(comentarioResponse: ComentarioResponse?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comentarios",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF409448)
            )

            // Mostrar estadísticas si están disponibles
            comentarioResponse?.estadisticas?.let { stats ->
                Text(
                    text = "(${stats.total_comentarios})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        comentarioResponse?.let { response ->
            if (response.comentarios.isEmpty()) {
                Text(
                    text = "No hay comentarios aún. ¡Sé el primero en comentar!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                response.comentarios.forEach { comentario ->
                    ComentarioItem(comentario = comentario)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ComentarioItem(comentario: ComentarioModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comentario.autor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF409448)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < comentario.valoracion) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatearFecha(comentario.fecha),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comentario.texto,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

fun formatearFecha(fechaISO: String): String {
    return try {
        val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val fechaParseada = fecha.parse(fechaISO)
        val formatoSalida = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatoSalida.format(fechaParseada ?: Date())
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}