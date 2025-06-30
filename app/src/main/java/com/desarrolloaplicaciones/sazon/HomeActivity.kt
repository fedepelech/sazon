package com.desarrolloaplicaciones.sazon

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.desarrolloaplicaciones.sazon.data.LoginRequest
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.ui.theme.SazonBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.desarrolloaplicaciones.sazon.data.RecentRecipeReturn
import com.desarrolloaplicaciones.sazon.data.RecetaConImagen
import com.desarrolloaplicaciones.sazon.data.Recipe
import com.desarrolloaplicaciones.sazon.data.completarImagenesRecetas

class HomeActivity : ComponentActivity() {
    fun getRecipesList(onRecipesFetched: (List<RecetaConImagen>) -> Unit) {
        val retrofit = RetrofitServiceFactory.makeRetrofitService()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = retrofit.getRecentRecipes()
                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        val recipesWithImages = completarImagenesRecetas(
                            retrofit,
                            response
                        );
                        onRecipesFetched(recipesWithImages)
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
    fun searchRecipes(query: String, onRecipesFetched: (List<RecetaConImagen>) -> Unit, onError: (String) -> Unit, excludeFilter: Boolean) {
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
                        val recipesWithImages = completarImagenesRecetas(
                            retrofit,
                            response
                        );
                        onRecipesFetched(recipesWithImages)
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

    // FUNCIÓN NUEVA: Búsqueda por tipo de receta
    fun searchRecipesByType(
        recipeType: String,
        onRecipesFetched: (List<RecetaConImagen>) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = RetrofitServiceFactory.makeRetrofitService()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = retrofit.getRecipesByType(recipeType)

                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        val recipesWithImages = completarImagenesRecetas(
                            retrofit,
                            response
                        );
                        onRecipesFetched(recipesWithImages)
                    } else {
                        onRecipesFetched(emptyList())
                        Toast.makeText(
                            this@HomeActivity,
                            "No se encontraron recetas para el tipo '$recipeType'",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMsg = if (e is retrofit2.HttpException && e.code() >= 400) {
                        "Error al buscar recetas por tipo: ${e.message}"
                    } else {
                        "Error de red: ${e.message}"
                    }
                    onError(errorMsg)
                    Toast.makeText(this@HomeActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getRecipeFilters(onFiltersFetched: (List<String>) -> Unit) {
        val retrofit = RetrofitServiceFactory.makeRetrofitService()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = retrofit.getRecipeTypes()
                withContext(Dispatchers.Main) {
                    val filterNames = response.map { it.nombre }
                    onFiltersFetched(filterNames)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFiltersFetched(emptyList())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se valida si esta autenticado
        val isAuthenticated = !TokenManager.getAccessToken().isNullOrEmpty()

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
    // Estados para manejar las recetas y el loading
    var recipes by remember { mutableStateOf<List<RecetaConImagen>>(emptyList()) }
    var filters by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentSearchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // ESTADOS PARA FILTROS: Para el filtro seleccionado y su índice
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var selectedFilterIndex by remember { mutableStateOf(-1) }

    // FUNCIÓN MODIFICADA: Para manejar la selección de filtros con índice
    val handleFilterSelection: (String, Int) -> Unit = { filterName, index ->
        selectedFilterIndex = index
        selectedFilter = if (filterName.isNotEmpty()) filterName else null
        currentSearchQuery = "" // Limpiar búsqueda por texto

        if (filterName.isNotEmpty()) {
            isLoading = true
            errorMessage = null

            activity.searchRecipesByType(
                recipeType = filterName,
                onRecipesFetched = { filteredRecipes ->
                    recipes = filteredRecipes
                    isLoading = false
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                }
            )
        } else {
            // Si no hay filtro, cargar todas las recetas
            isLoading = true
            activity.getRecipesList { fetchedRecipes ->
                recipes = fetchedRecipes
                isLoading = false
            }
        }
    }

    // Función para manejar búsquedas (MODIFICADA para resetear filtros)
    val handleSearch: (String, Boolean) -> Unit = { query, excludeFilter ->
        if (query.isNotEmpty()) {
            selectedFilter = null // Limpiar filtro seleccionado
            selectedFilterIndex = -1 // Resetear índice de filtro
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
            selectedFilter = null
            selectedFilterIndex = -1 // Resetear índice de filtro
            currentSearchQuery = ""
            isLoading = true
            activity.getRecipesList { fetchedRecipes ->
                recipes = fetchedRecipes
                isLoading = false
            }
        }
    }

    // LaunchedEffect para cargar las recetas y los filtros al iniciar la pantalla
    LaunchedEffect(Unit) {
        isLoading = true
        activity.getRecipesList { fetchedRecipes ->
            recipes = fetchedRecipes
            isLoading = false
        }
        activity.getRecipeFilters { fetchedFilters ->
            filters = fetchedFilters
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
            BottomNavigationBar(
                isAuthenticated,
                onProfileClick = {
                    context.startActivity(Intent(context, ProfileActivity::class.java))
                }
            )
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
                // MODIFICADO: Pasar el índice seleccionado y el callback actualizado
                FilterSection(
                    filters = filters,
                    selectedFilterIndex = selectedFilterIndex,
                    onFilterSelected = handleFilterSelection
                )
            }

            // MOSTRAR indicador de filtro activo
            if (selectedFilter != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filtrado por: $selectedFilter",
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            TextButton(
                                onClick = {
                                    selectedFilter = null
                                    selectedFilterIndex = -1 // RESETEAR ÍNDICE
                                    currentSearchQuery = ""
                                    isLoading = true
                                    activity.getRecipesList { fetchedRecipes ->
                                        recipes = fetchedRecipes
                                        isLoading = false
                                    }
                                }
                            ) {
                                Text(
                                    "Limpiar filtro",
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }
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
                // MODIFICADO: Mostrar mensajes personalizados para filtros y búsquedas
                if (selectedFilter != null && recipes.isEmpty()) {
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
                                    text = "No se encontraron recetas del tipo '$selectedFilter'",
                                    color = Color(0xFF7B1FA2),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        selectedFilter = null
                                        selectedFilterIndex = -1 // RESETEAR ÍNDICE
                                        currentSearchQuery = ""
                                        isLoading = true
                                        activity.getRecipesList { fetchedRecipes ->
                                            recipes = fetchedRecipes
                                            isLoading = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Text("Ver todas las recetas")
                                }
                            }
                        }
                    }
                } else if (currentSearchQuery.isNotEmpty() && recipes.isEmpty()) {
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
                                    onClick = {
                                        selectedFilter = null
                                        selectedFilterIndex = -1 // RESETEAR ÍNDICE
                                        currentSearchQuery = ""
                                        isLoading = true
                                        activity.getRecipesList { fetchedRecipes ->
                                            recipes = fetchedRecipes
                                            isLoading = false
                                        }
                                    },
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

// FUNCIÓN FILTRO COMPLETAMENTE CORREGIDA: Con state hoisting
@Composable
fun FilterSection(
    filters: List<String>,
    selectedFilterIndex: Int, // RECIBIR el índice seleccionado desde el padre
    onFilterSelected: (String, Int) -> Unit // CALLBACK modificado para pasar también el índice
) {
    if (filters.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(filters) { index, filterName ->
                FilterChip(
                    onClick = {
                        // Toggle logic: si ya está seleccionado, deseleccionar (-1), si no, seleccionar (index)
                        val newIndex = if (selectedFilterIndex == index) -1 else index
                        val filterToApply = if (newIndex != -1) filterName else ""
                        onFilterSelected(filterToApply, newIndex)
                    },
                    label = {
                        Text(
                            text = filterName,
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
}

@Composable
fun RecipeCard(recipe: RecetaConImagen) {

    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                val intent = Intent(context, ProductPageActivity::class.java).apply {
                    putExtra("recetaId", recipe.id)
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val token = TokenManager.getAccessToken()
        Box(modifier = Modifier.fillMaxSize()) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imagenUrl)
                    .crossfade(true)
                    .build()
            )
            // Imagen de placeholder
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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
fun BottomNavigationBar(
    isAuthenticated: Boolean,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current

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
                    IconButton(onClick = onProfileClick) {
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
