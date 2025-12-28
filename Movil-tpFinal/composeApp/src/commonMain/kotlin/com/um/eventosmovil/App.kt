package com.um.eventosmovil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.ui.eventos.EventoDetailScreen
import com.um.eventosmovil.ui.login.LoginScreen
import com.um.eventosmovil.ui.eventos.EventoListScreen

@Composable
fun App() {
    MaterialTheme {
        var storedToken by remember { mutableStateOf<String?>(null) }
        var selectedEvent by remember { mutableStateOf<EventoDTO?>(null) } // Nuevo estado

        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                storedToken == null -> {
                    LoginScreen(onLoginSuccess = { storedToken = it })
                }
                selectedEvent == null -> {
                    // Pantalla de Lista
                    EventoListScreen(
                        token = storedToken!!,
                        onNavigateBack = { storedToken = null },
                        onEventClick = { selectedEvent = it } // Al hacer clic, guardamos el evento
                    )
                }
                else -> {
                    // Pantalla de Detalle
                    EventoDetailScreen(
                        evento = selectedEvent!!,
                        onNavigateBack = { selectedEvent = null } // Al volver, limpiamos la selección
                    )
                }
            }
        }
    }
}
