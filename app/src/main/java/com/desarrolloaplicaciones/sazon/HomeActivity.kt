package com.desarrolloaplicaciones.sazon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desarrolloaplicaciones.sazon.ui.theme.SazonBackground

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SazonBackground),
                content = {
                    innerPadding -> RecipesScreen(
                        modifier = Modifier.padding(innerPadding).background(SazonBackground)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(SazonBackground),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reemplazar por imagotipo.
                        Image(
                            painter = painterResource(id = R.drawable.logo2),
                            contentDescription = "Logo de Sazón",
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sazón",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SazonBackground
                )
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Espacio debajo del header
            item {
                Spacer(modifier = Modifier.height(5.dp))
            }

            item {
                SearchSection()
            }

            item {
                FilterSection()
            }

            items(getRecipesList()) { recipe ->
                RecipeCard(recipe = recipe)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection() {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = {
                    Text(
                        "Buscar...",
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp)
    ) {
        // Aquí puedes agregar sugerencias de búsqueda si es necesario
    }
}

@Composable
fun FilterSection() {
    val filters = listOf("Filtro 1", "Filtro 2", "Filtro 3", "Filtro 4", "Filtro 5")
    var selectedFilterIndex by remember { mutableStateOf(0) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(filters) { index, filter ->
            FilterChip(
                onClick = { selectedFilterIndex = index },
                label = {
                    Text(
                        text = filter,
                        fontSize = 12.sp
                    )
                },
                selected = selectedFilterIndex == index,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilterIndex == index,
                    borderColor = if (selectedFilterIndex == index) Color(0xFF4CAF50) else Color.Gray,
                    selectedBorderColor = Color(0xFF4CAF50)
                )
            )
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                // Aquí puedes agregar la navegación al detalle de la receta
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagen de placeholder - reemplaza con tus imágenes reales
            Image(
                painter = painterResource(id = recipe.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente oscuro para mejorar legibilidad del texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Contenido de texto
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Estrellas de puntuación
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < recipe.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Gray,
        modifier = Modifier.height(80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Home
            IconButton(
                onClick = { /* Navegación Home */ },
                modifier = Modifier.size(48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Botón flotante central
            FloatingActionButton(
                onClick = { /* Acción agregar */ },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Botón Perfil
            IconButton(
                onClick = { /* Navegación Perfil */ },
                modifier = Modifier.size(48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

data class Recipe(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val rating: Int, // De 0 a 5 estrellas
    val description: String = "",
    val cookTime: String = ""
)

// Función para obtener datos de ejemplo
fun getRecipesList(): List<Recipe> {
    return listOf(
        Recipe(
            id = 1,
            title = "Omelette simple",
            imageRes = R.drawable.recipe1, // Placeholder
            rating = 5,
            description = "Delicioso omelette básico perfecto para el desayuno"
        ),
        Recipe(
            id = 2,
            title = "Omelette con espinaca",
            imageRes = R.drawable.recipe1, // Placeholder
            rating = 4,
            description = "Omelette nutritivo con espinacas frescas"
        ),
        Recipe(
            id = 3,
            title = "Omelette de queso",
            imageRes = R.drawable.recipe1, // Placeholder
            rating = 5,
            description = "Cremoso omelette con queso derretido"
        ),
        Recipe(
            id = 4,
            title = "Omelette de champiñones",
            imageRes = R.drawable.recipe1, // Placeholder
            rating = 4,
            description = "Sabroso omelette con champiñones salteados"
        ),
        Recipe(
            id = 5,
            title = "Omelette mixto",
            imageRes = R.drawable.recipe1, // Placeholder
            rating = 5,
            description = "Omelette completo con varios ingredientes"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewRecipesScreen() {
    RecipesScreen(
        modifier = Modifier
    )
}
