package com.desarrolloaplicaciones.sazon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.desarrolloaplicaciones.sazon.data.EmailRecovery
import com.desarrolloaplicaciones.sazon.data.RecoverPasswordRequest
import com.desarrolloaplicaciones.sazon.data.RecoverPasswordValidateRequest
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import com.desarrolloaplicaciones.sazon.data.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ChangePassActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            ChangePassScreen()
        }
    }
}



@Composable
fun ChangePassScreen() {
    val retrofitService = remember { RetrofitServiceFactory.makeRetrofitService() }

    var codigo by remember { mutableStateOf("") }
    var nuevaClave by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    val context = LocalContext.current
    val user_id = TokenManager.getUserId()
    var email = ""

    LaunchedEffect(Unit) {
        try {
            val mail = user_id?.let { retrofitService.obtenerUsuario(it).email }
            if (mail != null) {
                email = mail
            }
            val response = mail?.let { EmailRecovery(it) }?.let {
                retrofitService.recoverPassword(
                    RecoverPasswordRequest(email)
                )
            }
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d("API", "Código enviado exitosamente")
                } else {
                    Log.e("API", "Error al enviar código: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("API", "Excepción: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            SazonHeader()
        }
        Column {
            ChangePassBody(
                codigo = codigo,
                onCodigoChange = { codigo = it },
                nuevaClave = nuevaClave,
                onNuevaClaveChange = { nuevaClave = it },
                confirmacion = confirmacion,
                onConfirmacionChange = { confirmacion = it }
            )
            Spacer(modifier = Modifier.height(200.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { if (codigo.isBlank()) {
                    Log.e("Validación", "El código no puede estar vacío")
                    return@Button
                }

                    if (nuevaClave != confirmacion) {
                        Log.e("Validación", "Las contraseñas no coinciden")
                        return@Button
                    }

                    if (nuevaClave.isBlank() || confirmacion.isBlank()) {
                        Log.e("Validación", "Las contraseñas no pueden estar vacías")
                        return@Button
                    }

                    val request = RecoverPasswordValidateRequest(
                        email = email,
                        codigo = codigo,
                        nuevaClave = nuevaClave,
                        confirmacion = confirmacion
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = retrofitService.recoverPasswordValidate(request)
                            if (response.isSuccessful) {
                                Log.d("API", "Contraseña cambiada exitosamente")
                                TokenManager.clearCredentials()
                                context.startActivity(Intent(context, LoginActivity::class.java));
                            } else {
                                Log.e("API", "Error: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("API", "Excepción al validar clave: ${e.message}")
                        }
                    } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84F2A)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Actualizar", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BottomNavigationBar()
        }
    }
}

@Composable
fun ChangePassBody(codigo: String,
                   onCodigoChange: (String) -> Unit,
                   nuevaClave: String,
                   onNuevaClaveChange: (String) -> Unit,
                   confirmacion: String,
                   onConfirmacionChange: (String) -> Unit){
    Column(
        modifier = Modifier
            .background(Color(0xFFFDF5ED))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(16.dp))
            Text("Cambiar contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84F2A))
            Text("Se ha enviado un codigo al correo registrado", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = onCodigoChange,
                    label = { Text("Ingresar el codigo") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )
            }

            CustomTextField(label = "Nueva Contraseña", value = nuevaClave, enabled = true, onValueChange = onNuevaClaveChange)
            CustomTextField(label = "Nueva Contraseña", value = confirmacion, enabled = true, onValueChange = onConfirmacionChange)


            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePassScreenPreview() {
    MaterialTheme {
        ChangePassScreen()
    }
}