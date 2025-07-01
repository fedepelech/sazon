package com.desarrolloaplicaciones.sazon

import com.desarrolloaplicaciones.sazon.data.RecoverPasswordValidateRequest
import com.desarrolloaplicaciones.sazon.data.RetrofitServiceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.desarrolloaplicaciones.sazon.ui.theme.SazonBackground
import com.desarrolloaplicaciones.sazon.ui.theme.SazonRed
import com.desarrolloaplicaciones.sazon.ui.theme.SazonTheme

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val email = intent.getStringExtra("email") ?: ""

        setContent {
            SazonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResetPasswordScreen(modifier = Modifier.padding(innerPadding), email = email)
                }
            }
        }
    }
}

@Composable
fun ResetPasswordScreen(modifier: Modifier = Modifier, email: String) {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SazonBackground) // Added background color
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = stringResource(id = R.string.sazonlogo),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp)
        )
        // CORREGIDO: Campo de código de verificación
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Código de verificación",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            placeholder = {
                Text(
                    text = "Ingresa el código recibido por mail",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CORREGIDO: Campo de nueva contraseña
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Nueva contraseña",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            placeholder = {
                Text(
                    text = "Ingresa tu nueva contraseña",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CORREGIDO: Campo de confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Confirmar contraseña",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            placeholder = {
                Text(
                    text = "Confirma tu nueva contraseña",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validar que las contraseñas coincidan
                if (newPassword == confirmPassword && code.isNotBlank() && newPassword.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val service = RetrofitServiceFactory.makeRetrofitService()
                            val response = service.recoverPasswordValidate(
                                RecoverPasswordValidateRequest(
                                    email = email,
                                    codigo = code,
                                    nuevaClave = newPassword,
                                    confirmacion = confirmPassword
                                )
                            )

                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Contraseña restablecida con éxito",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Navegar de vuelta al login
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)

                                    // Cerrar la actividad
                                    if (context is ComponentActivity) {
                                        context.finish()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Ocurrió un error. Intente nuevamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Ocurrió un error. Por favor, intente nuevamente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    if (newPassword.isBlank()) {
                        Toast.makeText(
                            context,
                            "Ingrese la nueva contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (newPassword != confirmPassword) {
                        Toast.makeText(
                            context,
                            "Las contraseñas no coinciden. Inténtalo de nuevo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (code.isBlank()) {
                        Toast.makeText(
                            context,
                            "Ingrese el código",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            enabled = code.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SazonRed),
            elevation = ButtonDefaults.elevatedButtonElevation(6.dp)
        ) {
            Text(text = "Guardar", color = Color.White)
        }
    }
}
