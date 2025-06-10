package com.desarrolloaplicaciones.sazon

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.desarrolloaplicaciones.sazon.ui.theme.SazonBackground
import com.desarrolloaplicaciones.sazon.ui.theme.SazonPrimary
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // SazonTheme { // Se comenta para evitar que el tema del dispositivo sobreescriba el background
                // Asegura que el Scaffold y todo su contenido tengan el SazonBackground
                Scaffold(modifier = Modifier.fillMaxSize().background(SazonBackground)) { innerPadding ->
                    LoginScreen(
                        onLoginClicked = { email, password -> login(email, password) },
                        onGuestClicked = { /* Implementa la acción de invitado aquí */ },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            // }
        }
    }

    private fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Por favor, ingrese su email y contraseña", Toast.LENGTH_SHORT).show();
            return
        }

        val url = "http://192.168.0.62:3000/api/login"
        val json = """
        {
            "email": "$email",
            "password": "$pass"
        }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                        // Navegar a otra pantalla o continuar el flujo
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login fallido: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClicked: (String, String) -> Unit,
    onGuestClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 50.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = stringResource(id = R.string.sazonlogo),
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )

        Button(
            onClick = onGuestClicked,
            modifier = Modifier
                .width(200.dp)
                .height(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SazonPrimary
            )
        ) {
            Text(text = stringResource(id = R.string.ingresar_como_invitado), style = MaterialTheme.typography.labelMedium,)
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            placeholder = { Text(text = stringResource(id = R.string.nombre_de_usuario),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 0.dp)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            placeholder = { Text(text = stringResource(id = R.string.contrasena),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 0.dp)) },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            colors = textFieldColors,
            trailingIcon = {
                IconToggleButton(
                    checked = showPassword,
                    onCheckedChange = { showPassword = it } // 'it' is the new checked state
                ) {
                    Image(
                        painter = painterResource(
                            id = if (showPassword) R.drawable.visibility_off else R.drawable.visibility_on
                        ),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        )

        Button(
            onClick = { onLoginClicked(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SazonPrimary
            ),
            contentPadding = PaddingValues(15.dp)
        ) {
            Text(
                text = stringResource(id = R.string.iniciar_sesion),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    SazonTheme {
        LoginScreen(
            onLoginClicked = { _, _ -> /* No action needed for preview */ },
            onGuestClicked = {}
        )
    }
}
