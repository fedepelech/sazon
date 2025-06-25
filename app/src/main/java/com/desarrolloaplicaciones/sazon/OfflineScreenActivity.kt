// OfflineScreenActivity.kt
package com.desarrolloaplicaciones.sazon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import com.desarrolloaplicaciones.sazon.util.ConnectivityUtil

class OfflineScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SazonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5DC)
                ) {
                    OfflineScreenWithConnectivityCheck()
                }
            }
        }
    }
}

@Composable
fun OfflineScreenWithConnectivityCheck() {
    val context = LocalContext.current
    var isCheckingConnection by remember { mutableStateOf(false) }
    var connectionAttempts by remember { mutableStateOf(0) }

    // Efecto para verificar conectividad cada 10 segundos
    LaunchedEffect(Unit) {
        while (true) {
            isCheckingConnection = true
            connectionAttempts++

            // Usar la clase utilitaria estática
            val hasConnection = ConnectivityUtil.checkInternetConnection(context)

            if (hasConnection) {
                // Redirigir al login cuando hay conexión
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
                // Cerrar esta activity
                (context as? ComponentActivity)?.finish()
                break
            }

            isCheckingConnection = false
            delay(10_000) // Esperar 10 segundos antes del próximo chequeo
        }
    }

    OfflineScreen(
        isCheckingConnection = isCheckingConnection,
        connectionAttempts = connectionAttempts
    )
}

@Composable
fun OfflineScreen(
    modifier: Modifier = Modifier,
    isCheckingConnection: Boolean = false,
    connectionAttempts: Int = 0
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5DC))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Sazón
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Logo Sazón",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Texto principal "SIN CONEXION"
        Text(
            text = "SIN CONEXION",
            fontSize = 18.sp,
            color = Color(0xFFD2691E),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de estado de verificación
        Text(
            text = if (isCheckingConnection) "Verificando conexión..." else "Verificando cada 10 segundos",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Contador de intentos (opcional, para debugging)
        if (connectionAttempts > 0) {
            Text(
                text = "Intentos: $connectionAttempts",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OfflineScreenPreview() {
    SazonTheme {
        OfflineScreen(
            isCheckingConnection = false,
            connectionAttempts = 3
        )
    }
}
