package com.um.eventosmovil.ui.eventos

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.viewModel.EventListState
import com.um.eventosmovil.viewModel.EventListViewModel
import movil_tpfinal.composeapp.generated.resources.Res
import movil_tpfinal.composeapp.generated.resources.fondo2
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoListScreen(
    token: String,
    onNavigateBack: () -> Unit,
    onEventClick: (EventoDTO) -> Unit,
    onViewPurchases: () -> Unit // Parámetro para abrir Issue #69
) {
    val viewModel: EventListViewModel = viewModel { EventListViewModel(token) }
    val state by viewModel.state.collectAsState()

    val topAppBarViolet = Color(0xFF6750A4)
    val onTopAppBarViolet = Color.White

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
                    title = {
                        Text("Próximos Eventos", color = onTopAppBarViolet)
                    },
                    // BOTON ATRAS
                    navigationIcon = {
                        TextButton(onClick = { onNavigateBack() }) {
                            Text("< Atrás", color = Color.White)
                        }
                    },
                    // BOTÓN DE "MIS COMPRAS" A LA DERECHA
                    actions = {
                        TextButton(onClick = { onViewPurchases() }) {
                            Text("Mis Compras", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topAppBarViolet,
                        scrolledContainerColor = topAppBarViolet
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
                when (val current = state) {
                    is EventListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is EventListState.Error -> Text(
                        "Error: ${current.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                    is EventListState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(current.eventos) { evento ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { onEventClick(evento) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE1BEE7)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = evento.titulo,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color(0xFF4A148C)
                                        )
                                        Text(text = evento.fecha, style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = evento.resumen, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}