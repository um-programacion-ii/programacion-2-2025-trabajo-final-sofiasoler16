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
import com.um.eventosmovil.ui.eventos.MyPurchasesScreen
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
        var isViewingPurchases by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }
        var showErrorMessage by remember { mutableStateOf<String?>(null) }

        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                storedToken == null -> {
                    LoginScreen(onLoginSuccess = { token ->
                        storedToken = token
                        scope.launch {
                            val service = EventoService(token)
                            service.recuperarSesion().onSuccess { sesion ->
                                if (sesion != null) {
                                    // REANUDAMOS
                                    selectedEventId = sesion.eventoId
                                    seleccionTemporal = sesion.asientos.map { AsientoPosicion(it.fila, it.columna) }

                                    if (sesion.etapaActual == "DATOS_PERSONALES") {
                                        isAssigningNames = true
                                        isSelectingSeats = false
                                    } else {
                                        isSelectingSeats = true
                                    }
                                }
                            }
                        }
                    })
                }

                // 1. PRIORIDAD: Si está viendo compras, mostrar esa pantalla
                isViewingPurchases -> {
                    MyPurchasesScreen(
                        token = storedToken!!,
                        onNavigateBack = { isViewingPurchases = false }
                    )
                }

                selectedEventId != null && isAssigningNames -> {
                    NameAssignmentScreen(
                        asientos = seleccionTemporal,
                        onConfirmPurchase = { listaFinal ->
                            scope.launch {
                                val service = EventoService(storedToken!!)
                                service.realizarVenta(listaFinal).onSuccess {
                                    isAssigningNames = false
                                    selectedEventId = null
                                    showSuccessDialog = true // Muestra exito
                                }.onFailure {
                                    showErrorMessage = it.message ?: "Error al procesar la venta"
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
                        onEventClick = { evento -> selectedEventId = evento.id },
                        onViewPurchases = { isViewingPurchases = true }
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
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("¡Compra Exitosa!") },
                    text = { Text("Tus entradas han sido reservadas con éxito. Puedes verlas en 'Mis Compras'.") },
                    confirmButton = {
                        Button(onClick = { showSuccessDialog = false }) {
                            Text("Aceptar")
                        }
                    }
                )
            }

            if (showErrorMessage != null) {
                AlertDialog(
                    onDismissRequest = { showErrorMessage = null },
                    title = { Text("Hubo un problema") },
                    text = { Text(showErrorMessage!!) },
                    confirmButton = {
                        Button(onClick = { showErrorMessage = null }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    }
}