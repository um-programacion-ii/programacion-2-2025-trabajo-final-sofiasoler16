package com.um.eventosmovil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.ui.login.LoginScreen
import com.um.eventosmovil.ui.eventos.EventoListScreen
import com.um.eventosmovil.ui.eventos.EventoDetailScreen
import com.um.eventosmovil.ui.eventos.SeatSelectionScreen

@Composable
fun App() {
    MaterialTheme {
        var storedToken by remember { mutableStateOf<String?>(null) }
        var selectedEventId by remember { mutableStateOf<Long?>(null) }
        var isSelectingSeats by remember { mutableStateOf(false) }

        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                storedToken == null -> {
                    LoginScreen(onLoginSuccess = { token -> storedToken = token })
                }
                // PANTALLA SELECCION ASIENTOS
                selectedEventId != null && isSelectingSeats -> {
                    SeatSelectionScreen(
                        token = storedToken!!,
                        eventId = selectedEventId!!,
                        onNavigateBack = { isSelectingSeats = false },
                    )
                }
                // PANTALLA DETALLE EVENTO
                selectedEventId == null -> {
                    EventoListScreen(
                        token = storedToken!!,
                        onNavigateBack = { storedToken = null },
                        onEventClick = { evento -> selectedEventId = evento.id }
                    )
                }
                // PANTALLA LISTA EVENTOS
                else -> { // SI hace click muestra EventoDetail
                    EventoDetailScreen(
                        token = storedToken!!,
                        eventId = selectedEventId!!,
                        onNavigateBack = { selectedEventId = null },
                        onViewSeats = { isSelectingSeats = true }
                    )
                }
            }
        }
    }
}