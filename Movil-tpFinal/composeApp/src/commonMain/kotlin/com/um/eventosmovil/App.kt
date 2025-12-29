package com.um.eventosmovil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.um.eventosmovil.data.AsientoPosicion
import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.service.EventoService
import com.um.eventosmovil.ui.login.LoginScreen
import com.um.eventosmovil.ui.eventos.EventoListScreen
import com.um.eventosmovil.ui.eventos.EventoDetailScreen
import com.um.eventosmovil.ui.eventos.NameAssignmentScreen
import com.um.eventosmovil.ui.eventos.SeatSelectionScreen
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        var storedToken by remember { mutableStateOf<String?>(null) }
        var selectedEventId by remember { mutableStateOf<Long?>(null) }
        var isSelectingSeats by remember { mutableStateOf(false) }
        var isAssigningNames by remember { mutableStateOf(false) }
        var seleccionTemporal by remember { mutableStateOf<List<AsientoPosicion>>(emptyList()) }
        val scope = rememberCoroutineScope()

        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                storedToken == null -> {
                    LoginScreen(onLoginSuccess = { token -> storedToken = token })
                }

                selectedEventId != null && isAssigningNames -> {
                    NameAssignmentScreen(
                        asientos = seleccionTemporal,
                        onConfirmPurchase = { listaFinal ->
                            scope.launch {
                                val service = EventoService(storedToken!!)
                                service.realizarVenta(listaFinal).onSuccess {
                                    // SI venta exitosa, vuelvo a listar eventos
                                    isAssigningNames = false
                                    selectedEventId = null
                                }.onFailure {
                                    println("Error en la compra: ${it.message}")
                                }
                            }
                        }
                    )
                }
                // PANTALLA SELECCION ASIENTOS
                selectedEventId != null && isSelectingSeats -> {
                    SeatSelectionScreen(
                        token = storedToken!!,
                        eventId = selectedEventId!!,
                        onNavigateBack = { isSelectingSeats = false },
                        onConfirmSelection = { lista ->
                            seleccionTemporal = lista
                            isAssigningNames = true
                            isSelectingSeats = false
                        }
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