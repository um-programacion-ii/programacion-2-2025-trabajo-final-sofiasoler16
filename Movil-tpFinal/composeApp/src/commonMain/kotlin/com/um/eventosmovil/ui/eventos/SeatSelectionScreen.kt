package com.um.eventosmovil.ui.eventos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import com.um.eventosmovil.data.AsientoPosicion
import com.um.eventosmovil.service.EventoService
import com.um.eventosmovil.viewModel.SeatSelectionViewModel
import com.um.eventosmovil.viewModel.SeatState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    token: String,
    eventId: Long,
    onNavigateBack: () -> Unit,
    onConfirmSelection: (List<AsientoPosicion>) -> Unit
) {
    val viewModel: SeatSelectionViewModel = viewModel(key = eventId.toString()) {
        SeatSelectionViewModel(EventoService(token), eventId)
    }
    val state by viewModel.state.collectAsState()

    // --- NUEVOS ESTADOS PARA QUE VEAS QUÉ PASA --- [cite: 2025-12-29]
    var estaCargando by remember { mutableStateOf(false) }
    var mensajeErrorUI by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona Asientos", color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("< Atrás", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6750A4)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val current = state) {
                is SeatState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is SeatState.Error -> Text(current.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                is SeatState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Mapa de Asientos", style = MaterialTheme.typography.titleLarge)
                        Text("Seleccionados: ${current.seleccionados.size} / 4", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- MENSAJE DE ERROR VISIBLE EN EL CELULAR --- [cite: 2025-12-29]
                        if (mensajeErrorUI != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = mensajeErrorUI!!,
                                    color = Color.Red,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dibujo de la Matriz
                        for (f in 0 until current.mapa.filas) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (c in 0 until current.mapa.columnas) {
                                    val estado = current.mapa.matriz[f][c]
                                    val esSeleccionado = current.seleccionados.contains(AsientoPosicion(f, c))

                                    val colorAsiento = when {
                                        esSeleccionado -> Color(0xFF6750A4)
                                        estado == "VENDIDO" -> Color.Red
                                        estado == "BLOQUEADO" -> Color(0xFFFF9800)
                                        else -> Color.LightGray
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(35.dp)
                                            .padding(3.dp)
                                            .background(
                                                color = colorAsiento,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable(enabled = estado == "LIBRE") {
                                                viewModel.toggleSeat(f, c)
                                            }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- INDICADOR DE CARGA O BOTÓN --- [cite: 2025-12-29]
                        if (estaCargando) {
                            CircularProgressIndicator(color = Color(0xFF6750A4))
                        } else {
                            Button(
                                onClick = {
                                    estaCargando = true
                                    mensajeErrorUI = null
                                    viewModel.confirmarSeleccion(
                                        onSuccess = {
                                            estaCargando = false
                                            onConfirmSelection(current.seleccionados.toList())
                                        },
                                        onError = { mensaje ->
                                            estaCargando = false
                                            mensajeErrorUI = "Error: $mensaje"
                                        }
                                    )
                                },
                                enabled = current.seleccionados.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                            ) {
                                Text("Confirmar Selección")
                            }
                        }
                    }
                }
            }
        }
    }
}