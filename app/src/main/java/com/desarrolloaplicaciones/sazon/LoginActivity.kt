package com.desarrolloaplicaciones.sazon

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones.sazon.data.LoginRequest
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import com.desarrolloaplicaciones.sazon.ui.theme.SazonBackground
import com.desarrolloaplicaciones.sazon.ui.theme.SazonPrimary
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme
import com.desarrolloaplicaciones.sazon.util.ConnectivityUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {
    private var isLoading = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        TokenManager.removeToken()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier
                .fillMaxSize()
                .background(SazonBackground)) { innerPadding ->
                LoginScreen(
                    onLoginClicked = { email, password -> attemptLogin(email, password) },
                    onGuestClicked = {
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent);
                        finish();
                    },
                    modifier = Modifier.padding(innerPadding),
                    isLoading = isLoading
                )
            }
        }
    }

    fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun login(email: String, pass: String) {
        val retrofit = RetrofitServiceFactory.makeRetrofitService();
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Por favor, ingrese su email y contraseña", Toast.LENGTH_SHORT).show();
            return
        }

//        val json = """
//        {
//            "email": "$email",
//            "password": "$pass"
//        }
//        """.trimIndent()
        
        val body = LoginRequest(email, pass);
        lifecycleScope.launch(Dispatchers.IO) {
            isLoading.value = true
            try {
                val response = retrofit.login(body)
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (!response.token.isNullOrEmpty()) {
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        TokenManager.saveToken(response.token);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login fallido: ${response}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (e is retrofit2.HttpException && e.code() >= 400) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login fallido: email o contraseña incorrecta",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error de red: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun attemptLogin(email: String, password: String) {
        if (!ConnectivityUtil.checkInternetConnection(this)) {
            val intent = Intent(this, OfflineScreenActivity::class.java)
            startActivity(intent)
        } else {
            // Hay conexión - proceder con el login
            login(email, password)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClicked: (String, String) -> Unit,
    onGuestClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: State<Boolean>,
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
                .padding(bottom = 32.dp)
                .background(MaterialTheme.colorScheme.background),
            contentScale = ContentScale.Fit
        )

        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(50.dp),
                color = SazonPrimary
            )
        } else {
            Button(
                onClick = onGuestClicked,
                modifier = Modifier
                    .width(200.dp)
                    .height(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SazonPrimary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.ingresar_como_invitado),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
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
//            onClick = { onLoginClicked(email, password) },
            onClick = { onLoginClicked("fede@ejemplo.com", "12345") },
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
    val isLoading = remember { mutableStateOf(false) }
    SazonTheme {
        LoginScreen(
            onLoginClicked = { _, _ -> /* No action needed for preview */ },
            onGuestClicked = {},
            isLoading = isLoading
        )
    }
}
