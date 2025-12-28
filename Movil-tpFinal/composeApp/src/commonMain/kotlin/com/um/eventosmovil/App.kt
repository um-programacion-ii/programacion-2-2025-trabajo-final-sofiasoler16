package com.um.eventosmovil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.um.eventosmovil.ui.login.LoginScreen
import com.um.eventosmovil.ui.eventos.EventoListScreen

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
                EventoListScreen(
                    token = storedToken!!,
                    onNavigateBack = { storedToken = null })
            }
        }
    }
}
