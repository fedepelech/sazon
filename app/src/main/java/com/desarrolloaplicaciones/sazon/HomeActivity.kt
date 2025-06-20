package com.desarrolloaplicaciones.sazon

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.desarrolloaplicaciones.sazon.LoginActivity
import com.desarrolloaplicaciones.sazon.data.LoginRequest
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.ui.theme.SazonBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import com.desarrolloaplicaciones.sazon.data.RecentRecipeReturn

class HomeActivity : ComponentActivity() {
    fun getRecipesList(onRecipesFetched: (List<Recipe>) -> Unit) {
        val retrofit = RetrofitServiceFactory.makeRetrofitService()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = retrofit.getRecentRecipes()
                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        val recipes = response.map { recipeResponse ->
                            Recipe(
                                id = recipeResponse.id,
                                nombre = recipeResponse.nombre,
                                imageRes = R.drawable.recipe1,
                                createdAt = recipeResponse.createdAt
                            )
                        }
                        onRecipesFetched(recipes)
                    } else {
                        Toast.makeText(
                            this@HomeActivity, // Ahora funciona correctamente
                            "No se encontraron recetas.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (e is retrofit2.HttpException && e.code() >= 400) {
                        Toast.makeText(
                            this@HomeActivity, // Contexto accesible
                            "Error al cargar las recetas: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Error de red: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se valida si esta autenticado
        var isAuthenticated = if (TokenManager.getAccessToken().isNullOrEmpty()) false else true



        setContent {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SazonBackground),
                content = { innerPadding ->
                    HomeScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(SazonBackground),
                        isAuthenticated = isAuthenticated,
                        activity = this@HomeActivity // Pasamos la referencia de la actividad
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    isAuthenticated: Boolean,
    activity: HomeActivity // Recibimos la actividad como parámetro
) {
    // Estado para manejar las recetas y el loading
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect para cargar las recetas al iniciar la pantalla
    LaunchedEffect(Unit) {
        activity.getRecipesList { fetchedRecipes ->
            recipes = fetchedRecipes
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SazonBackground),
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
            BottomNavigationBar(isAuthenticated)
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

            // Mostrar loading, error o las recetas
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            } else {
                // Mostrar las recetas cargadas
                items(recipes) { recipe ->
                    RecipeCard(recipe = recipe)
                }
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
                    text = recipe.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Estrellas de puntuación (comentadas por ahora)
                /*
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
                */
            }
        }
    }
}

@Composable
fun BottomNavigationBar(isAuthenticated: Boolean) {
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

            // Botón Perfil o espacio vacío
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAuthenticated) {
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
    }
}

data class Recipe(
    val id: String,
    val nombre: String,
    val imageRes: Int,
    val createdAt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    // Para el preview, creamos una actividad mock
    // HomeScreen(
    //     modifier = Modifier,
    //     isAuthenticated = true,
    //     activity = // No se puede mostrar en preview
    // )
}
