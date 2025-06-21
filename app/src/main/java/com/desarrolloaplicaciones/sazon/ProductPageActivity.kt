package com.desarrolloaplicaciones.sazon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.net.toUri

// Clases de datos para la app
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

// Clase principal de la actividad
class ProductPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Datos de ejemplo para mostrar
        val recetaEjemplo = Receta(
            nombre = "Tarta de Manzana",
            tipo_id = 1,
            dificultad_id = 2,
            descripcion = "Deliciosa tarta casera",
            ingredientes = listOf(
                Ingrediente("Harina", 200, "g"),
                Ingrediente("Azúcar", 100, "g")
            ),
            pasos = listOf(
                Paso(1, "Mezclar los ingredientes secos."),
                Paso(2, "Hornear a 180°C durante 30 minutos.")
            )
        )

        setContent {
            val scrollState = rememberScrollState()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFDF6EB))
                    .verticalScroll(scrollState)
            ) {
                ImagenSazon()
                RecetaTitulo(nombre = recetaEjemplo.nombre)
                RecetaUtility()
                ImagenProducto()
                ListaIngredientes(ingredientes = recetaEjemplo.ingredientes)
                ListaPasos(pasos = recetaEjemplo.pasos)
                VideoReceta(videoUrl = "https://www.youtube.com/watch?v=ejemplo")
                CalculadoraIngredientes(ingredientes = recetaEjemplo.ingredientes)
                AgregarComentario()
                ListaComentarios()
            }
        }
    }
}

// Componentes UI - Fuera de la clase principal
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
fun RecetaUtility() {
    var isFavorite by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Botón de favoritos con estado
        IconButton(
            onClick = {
                isFavorite = !isFavorite
                // Aquí se agregaría la lógica para conectar con el backend
                // por ejemplo: viewModel.toggleFavorite(recetaId)
            }
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                tint = if (isFavorite) Color(0xFF409448) else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        // Sistema de calificación de 5 estrellas
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..5) {
                IconButton(
                    onClick = { rating = i },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificación $i",
                        tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        // Nombre del usuario
        Text(
            text = "nombreUser",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ImagenProducto() {
    Image(
        painter = painterResource(id = R.drawable.logo2),
        contentDescription = "Imagen del Producto",
        modifier = Modifier.size(100.dp)
    )
}

@Composable
fun ListaIngredientes(ingredientes: List<Ingrediente>) {
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
fun ListaPasos(pasos: List<Paso>) {
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
                    text = "${paso.paso_numero}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "。${paso.descripcion}",
                    color = Color.Black
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
    var ingredientesAjustados by remember { mutableStateOf(ingredientes) }
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
                    // Selector de metodo de calculo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Método:")
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = modoCalculo == 0,
                                onClick = { modoCalculo = 0 }
                            )
                            Text(
                                text = "Mitad o doble",
                                modifier = Modifier.clickable { modoCalculo = 0 }
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = modoCalculo == 1,
                                onClick = { modoCalculo = 1 }
                            )
                            Text(
                                text = "Porciones",
                                modifier = Modifier.clickable { modoCalculo = 1 }
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = modoCalculo == 2,
                                onClick = { modoCalculo = 2 }
                            )
                            Text(
                                text = "Ingrediente",
                                modifier = Modifier.clickable { modoCalculo = 2 }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Según el modo seleccionado, mostrar diferentes controles
                    when (modoCalculo) {
                        0 -> {
                            // Factor multiplicador
                            Text("Selecciona la cantidad:")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { factorMultiplicador = 0.5f },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (factorMultiplicador == 0.5f)
                                            Color(0xFF409448) else Color.Gray
                                    )
                                ) {
                                    Text("Mitad")
                                }
                                Button(
                                    onClick = { factorMultiplicador = 1f },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (factorMultiplicador == 1f)
                                            Color(0xFF409448) else Color.Gray
                                    )
                                ) {
                                    Text("Original")
                                }
                                Button(
                                    onClick = { factorMultiplicador = 2f },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (factorMultiplicador == 2f)
                                            Color(0xFF409448) else Color.Gray
                                    )
                                ) {
                                    Text("Doble")
                                }
                            }
                        }
                        1 -> {
                            // Ajuste por porciones
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Receta original para $porcionesOriginales porciones",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            OutlinedTextField(
                                value = porcionesDeseadas,
                                onValueChange = {
                                    if (it.isEmpty() || it.toIntOrNull() != null) {
                                        porcionesDeseadas = it
                                    }
                                },
                                label = { Text("Porciones deseadas") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        2 -> {
                            // Ajuste por ingrediente específico
                            Text("Selecciona un ingrediente:")
                            ingredientes.forEachIndexed { index, ingrediente ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        ingredienteSeleccionado = ingrediente
                                    }
                                ) {
                                    RadioButton(
                                        selected = ingredienteSeleccionado == ingrediente,
                                        onClick = { ingredienteSeleccionado = ingrediente }
                                    )
                                    Text("${ingrediente.nombre} (${ingrediente.cantidad} ${ingrediente.unidad})")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            ingredienteSeleccionado?.let {
                                OutlinedTextField(
                                    value = cantidadAjustada,
                                    onValueChange = {
                                        if (it.isEmpty() || it.toIntOrNull() != null) {
                                            cantidadAjustada = it
                                        }
                                    },
                                    label = { Text("Nueva cantidad de ${it.nombre}") },
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
                            porcionesDeseadas.toFloat() / porcionesOriginales
                        else 1f
                        2 -> {
                            if (ingredienteSeleccionado != null && cantidadAjustada.isNotEmpty()) {
                                cantidadAjustada.toFloat() / ingredienteSeleccionado!!.cantidad
                            } else 1f
                        }
                        else -> 1f
                    }

                    ingredientes.forEach { ingrediente ->
                        val nuevaCantidad = (ingrediente.cantidad * factor).toInt()
                        Text("• $nuevaCantidad ${ingrediente.unidad} de ${ingrediente.nombre}")
                    }

                    if (modoCalculo == 2 || modoCalculo == 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Porciones resultantes: ${(porcionesOriginales * factor).toInt()}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AgregarComentario() {
    val (texto, setTexto) = remember { mutableStateOf("") }

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

            OutlinedTextField(
                value = texto,
                onValueChange = setTexto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Escribe tu comentario aquí...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF409448),
                    unfocusedBorderColor = Color(0xFFBDBDBD)
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /* Lógica para enviar comentario */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF409448)
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun ListaComentarios() {
    // Lista de comentarios de ejemplo
    val comentarios = listOf(
        Comentario("María Gómez", "¡Me encantó esta receta! La hice ayer y quedó deliciosa.", "2 horas"),
        Comentario("Juan Pérez", "¿Se puede sustituir la cebolla por puerro?", "5 horas"),
        Comentario("Ana López", "La hice con menos sal y quedó perfecta. Gracias por compartir.", "1 día")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        comentarios.forEach { comentario ->
            ComentarioBurbuja(comentario)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ComentarioBurbuja(comentario: Comentario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar o icono de usuario
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF409448))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comentario.nombre.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = comentario.nombre,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = comentario.tiempo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = comentario.texto,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Vista previa
@Preview(showBackground = true)
@Composable
fun PreviewComponent() {
    val recetaEjemplo = Receta(
        nombre = "Tarta de Manzana",
        tipo_id = 1,
        dificultad_id = 2,
        descripcion = "Deliciosa tarta casera",
        ingredientes = listOf(
            Ingrediente("Harina", 200, "g"),
            Ingrediente("Azúcar", 100, "g")
        ),
        pasos = listOf(
            Paso(1, "Mezclar los ingredientes secos."),
            Paso(2, "Hornear a 180°C durante 30 minutos.")
        )
    )

    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFDF6EB))
            .verticalScroll(scrollState)
    ) {
        ImagenSazon()
        RecetaTitulo(nombre = recetaEjemplo.nombre)
        RecetaUtility()
        ImagenProducto()
        ListaIngredientes(ingredientes = recetaEjemplo.ingredientes)
        ListaPasos(pasos = recetaEjemplo.pasos)
        VideoReceta(videoUrl = "https://www.youtube.com/watch?v=ejemplo")
        CalculadoraIngredientes(ingredientes = recetaEjemplo.ingredientes)
        AgregarComentario()
        ListaComentarios()
    }
}