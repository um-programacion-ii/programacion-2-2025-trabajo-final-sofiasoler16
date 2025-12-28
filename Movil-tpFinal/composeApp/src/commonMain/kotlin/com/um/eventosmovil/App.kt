package com.um.eventosmovil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.um.eventosmovil.ui.login.LoginScreen

@Composable
fun App() {
    MaterialTheme {
        // Estado de navegación: Si el token es nulo, estamos en Login
        var storedToken by remember { mutableStateOf<String?>(null) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (storedToken == null) {
                LoginScreen(onLoginSuccess = { token ->
                    storedToken = token
                })
            } else {
                WelcomeScreen(onLogout = { storedToken = null })
            }
        }
    }
}

@Composable
fun WelcomeScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium)
        Text("Sesión iniciada correctamente.")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Cerrar Sesión")
        }
    }
}