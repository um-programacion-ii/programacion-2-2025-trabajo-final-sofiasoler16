package com.um.eventosmovil.ui.eventos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.um.eventosmovil.service.EventoService
import com.um.eventosmovil.viewModel.EventDetailState
import com.um.eventosmovil.viewModel.EventDetailViewModel
import movil_tpfinal.composeapp.generated.resources.Res
import movil_tpfinal.composeapp.generated.resources.fondo2
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetailScreen(
    token: String,
    eventId: Long,
    onNavigateBack: () -> Unit,
    onViewSeats: () -> Unit
) {
// Agrega una Key (llave unica) para no reutilizar el mismo viewModel y ver distintos eventosDetail
    val viewModel: EventDetailViewModel = viewModel(key = eventId.toString()) {
        EventDetailViewModel(EventoService(token), eventId)
    }

    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Detalle", color = Color.White) },
                    navigationIcon = {
                        TextButton(onClick = onNavigateBack) {
                            Text("< Atrás", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6750A4))
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val current = state) {
                    is EventDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is EventDetailState.Success -> {
                        val evento = current.evento
                        Card(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(evento.titulo, style = MaterialTheme.typography.headlineSmall)
                                Text("$${evento.precioEntrada}", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("📍 ${evento.direccion}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Descripción", style = MaterialTheme.typography.labelLarge)
                                Text(evento.descripcion, style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onViewSeats,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                ) {
                                    Text("Ver asientos")
                                }
                            }
                        }
                    }
                    is EventDetailState.Error -> Text("Error: ${current.message}", color = Color.Red)
                }
            }
        }
    }
}