package com.desarrolloaplicaciones.sazon

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
                            this@HomeActivity,
                            "No se encontraron recetas.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (e is retrofit2.HttpException && e.code() >= 400) {
                        Toast.makeText(
                            this@HomeActivity,
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

    // Nueva función específica para búsquedas
    fun searchRecipes(query: String, onRecipesFetched: (List<Recipe>) -> Unit, onError: (String) -> Unit, excludeFilter: Boolean) {
        val retrofit = RetrofitServiceFactory.makeRetrofitService()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = if (excludeFilter) {
                    retrofit.getRecipesWithExcludeFilter(query)
                } else {
                    retrofit.getRecipesWithIncludeFilter(query)
                }

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
                        onRecipesFetched(emptyList())
                        Toast.makeText(
                            this@HomeActivity,
                            "No se encontraron recetas para '$query'",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMsg = if (e is retrofit2.HttpException && e.code() >= 400) {
                        "Error al buscar recetas: ${e.message}"
                    } else {
                        "Error de red: ${e.message}"
                    }
                    onError(errorMsg)
                    Toast.makeText(this@HomeActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se valida si esta autenticado
        var isAuthenticated = if (TokenManager.getAccessToken().isNullOrEmpty()) false else true

        enableEdgeToEdge()
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
                        activity = this@HomeActivity
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
    activity: HomeActivity
) {
    // Estado para manejar las recetas y el loading
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentSearchQuery by remember { mutableStateOf("") }

    // Función para manejar búsquedas
    val handleSearch: (String, Boolean) -> Unit = { query, excludeFilter ->
        if (query.isNotEmpty()) {
            currentSearchQuery = query
            isLoading = true
            errorMessage = null

            activity.searchRecipes(
                query = query,
                onRecipesFetched = { searchResults ->
                    recipes = searchResults
                    isLoading = false
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                },
                excludeFilter = excludeFilter
            )
        } else {
            // Si no hay query, cargar todas las recetas
            currentSearchQuery = ""
            isLoading = true
            activity.getRecipesList { fetchedRecipes ->
                recipes = fetchedRecipes
                isLoading = false
            }
        }
    }

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
            item {
                Spacer(modifier = Modifier.height(5.dp))
            }

            item {
                SearchSection(
                    onSearch = handleSearch,
                    currentQuery = currentSearchQuery
                )
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
                // Mostrar mensaje si no hay resultados de búsqueda
                if (currentSearchQuery.isNotEmpty() && recipes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No se encontraron recetas para '$currentSearchQuery'",
                                    color = Color(0xFF7B1FA2),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { handleSearch("", false) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Text("Ver todas las recetas")
                                }
                            }
                        }
                    }
                }

                // Mostrar las recetas
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
fun SearchSection(
    onSearch: (String, Boolean) -> Unit,
    currentQuery: String = ""
) {
    var searchText by remember { mutableStateOf(currentQuery) }
    var excludeFilter by remember { mutableStateOf(false) }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { query ->
                    onSearch(query.trim(), excludeFilter)
                },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text("Buscar recetas...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = excludeFilter,
                            onCheckedChange = { excludeFilter = it }
                        )
                        Text("Excluir", fontSize = 12.sp)
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = { },
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sin contenido expandido para evitar problemas de constraints
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
