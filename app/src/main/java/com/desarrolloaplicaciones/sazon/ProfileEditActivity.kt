package com.desarrolloaplicaciones.sazon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType




@Composable
fun ProfileEditScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5ED)), // Fondo crema claro
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            SazonHeader()
        }
        Column {
            ProfileEditBodySection()
            Spacer(modifier = Modifier.height(32.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            BottomNavigationBar()
        }
    }
}

@Composable
fun ProfileEditBodySection() {
    Column(
        modifier = Modifier
            .background(Color(0xFFFDF5ED))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(16.dp))
            Text("Mi cuenta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84F2A))
            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(label = "Nombre", value = "Lucas Castro")
            CustomTextField(label = "Email", value = "lhlcastro@gmail.com", keyboardType = KeyboardType.Email)
            CustomTextField(label = "Alias", value = "@lcastro")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = "**********",
                    onValueChange = {},
                    label = { Text("Contraseña") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )
                Button(
                    onClick = { /* TODO: Acción editar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84F2A))
                ) {
                    Text("Editar", color = Color.White)
                }
            }

            CustomTextField(label = "Fecha de nacimiento", value = "10/07/1998", keyboardType = KeyboardType.Number)
            CustomTextField(label = "Provincia", value = "Santa Fe")
            CustomTextField(label = "Localidad", value = "Rosario")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun CustomTextField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun SazonHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = stringResource(id = R.string.sazonlogo),
            modifier = Modifier.size(125.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    MaterialTheme {
        ProfileEditScreen()
    }
}