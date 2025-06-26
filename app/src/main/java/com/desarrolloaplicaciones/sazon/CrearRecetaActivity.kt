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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas

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
    val categorias = listOf("Desayuno", "Almuerzo", "Cena", "Postre", "Snack")
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val unidades = listOf("ml", "gr", "un")
    var ingredientes by remember { mutableStateOf(listOf(IngredienteInput())) }
    var pasos by remember { mutableStateOf(listOf(PasoInput())) }

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
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categorias.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = { Text(categoria) },
                                        onClick = {
                                            categoriaSeleccionada = categoria
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                ingredientes.forEachIndexed { index, ingrediente ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = ingrediente.nombre,
                            onValueChange = { newValue ->
                                ingredientes = ingredientes.toMutableList().also {
                                    it[index] = it[index].copy(nombre = newValue)
                                }
                            },
                            label = { Text("Ingrediente") },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = ingrediente.cantidad,
                            onValueChange = { newValue ->
                                ingredientes = ingredientes.toMutableList().also {
                                    it[index] = it[index].copy(cantidad = newValue)
                                }
                            },
                            label = { Text("Cant") },
                            modifier = Modifier.width(80.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        var unidadExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = unidadExpanded,
                            onExpandedChange = { unidadExpanded = !unidadExpanded }
                        ) {
                            OutlinedTextField(
                                value = ingrediente.unidad,
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
                            onClick = { /* TODO: Acción validar */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF409448))
                        ) {
                            Text("Subir Imagenes", color = Color.White)
                        }
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
                            onClick = { /* TODO: Acción validar */ },
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

@Preview(showBackground = true)
@Composable
fun CrearRecetaPreview() {
    CrearRecetaScreen()
}