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



@Composable
fun ChangePassScreen() {
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
            ChangePassBody()
            Spacer(modifier = Modifier.height(200.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { /* TODO: actualizar */ },
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
fun ChangePassBody(){
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
                    value = "Codigo",
                    onValueChange = {},
                    label = { Text("Ingresar el codigo") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )
                Button(
                    onClick = { /* TODO: Acción validar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84F2A))
                ) {
                    Text("Validar", color = Color.White)
                }
            }

            CustomTextField(label = "Nueva Contraseña", value = "Ingrese la nueva contraseña")
            CustomTextField(label = "Nueva Contraseña", value = "Ingrese la nueva contraseña")


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