package com.desarrolloaplicaciones.sazon



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            PerfilScreen()
        }
    }
}


@Composable
fun PerfilScreen() {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            HeaderSection(name = "Lucas", username = "@lcastro")
            Spacer(modifier = Modifier.height(32.dp))
            MenuItem(icon = Icons.Default.AccountCircle, text = "Mis recetas"){
                context.startActivity(Intent(context, MisRecetasActivity::class.java))
            }
            MenuItem(icon = Icons.Default.AccountCircle, text = "Recetas guardadas"){
                context.startActivity(Intent(context, RecetasGuardadasActivity::class.java))
            }
            MenuItem(icon = Icons.Default.AccountCircle, text = "Mi cuenta"){
                context.startActivity(Intent(context, ProfileEditActivity::class.java))
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { /* TODO: acción cerrar sesión */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84F2A)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Cerrar sesión", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BottomNavigationBar()
        }
    }
}

@Composable
fun HeaderSection(name: String, username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD3CDC6))
            .height(200.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Hola $name!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(username, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 22.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color(0xFFD84F2A)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = Color(0xFFD84F2A), fontWeight = FontWeight.Bold, fontSize = 25.sp)
    }
}

@Composable
fun BottomNavigationBar() {
    val context = LocalContext.current
    BottomAppBar(
        containerColor = Color(0xFFFDF5ED),
        tonalElevation = 4.dp,
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        IconButton(onClick = {context.startActivity(Intent(context, ProfileActivity::class.java))}) {
            Icon(Icons.Default.Home, contentDescription = "Inicio")
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {context.startActivity(Intent(context, ProfileActivity::class.java))}) {
            Icon(Icons.Default.AddCircle, contentDescription = "Agregar")
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {context.startActivity(Intent(context, ProfileActivity::class.java))}) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Más")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    MaterialTheme {
        PerfilScreen()
    }
}



