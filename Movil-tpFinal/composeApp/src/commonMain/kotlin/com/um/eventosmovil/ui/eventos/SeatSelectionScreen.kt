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
    onNavigateBack: () -> Unit
) {
    val viewModel: SeatSelectionViewModel = viewModel(key = eventId.toString()) {
        SeatSelectionViewModel(EventoService(token), eventId)
    }
    val state by viewModel.state.collectAsState()

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

                        Spacer(modifier = Modifier.height(24.dp))

                        for (f in 0 until current.mapa.filas) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (c in 0 until current.mapa.columnas) {
                                    val estado = current.mapa.matriz[f][c]
                                    val esSeleccionado = current.seleccionados.contains(AsientoPosicion(f, c))

                                    // Colores según estado solicitado en el Issue #30
                                    val colorAsiento = when {
                                        esSeleccionado -> Color(0xFF6750A4) // Violeta (Selección)
                                        estado == "VENDIDO" -> Color.Red
                                        estado == "BLOQUEADO" -> Color(0xFFFF9800) // Naranja
                                        else -> Color.LightGray // LIBRE
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

                        Button(
                            onClick = { /* Próximo: Ir a confirmación de nombres */ },
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