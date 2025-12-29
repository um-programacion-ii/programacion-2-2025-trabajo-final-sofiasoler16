package com.um.eventosmovil.ui.eventos


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import com.um.eventosmovil.service.EventoService
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.um.eventosmovil.data.VentaDTO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPurchasesScreen(token: String, onNavigateBack: () -> Unit) {
    val service = remember { EventoService(token) }
    var ventas by remember { mutableStateOf<List<VentaDTO>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) } // Estado para errores

    LaunchedEffect(Unit) {
        service.getMisVentas().onSuccess {
            ventas = it.reversed()
            loading = false
        }.onFailure {
            errorMsg = "No se pudieron cargar las compras"
            loading = false
        }
    }

    Scaffold(
        topBar = {
            // USAMOS TopAppBar (en lugar de SmallTopAppBar)
            TopAppBar(
                title = { Text("Mis Compras", color = Color.White) },
                navigationIcon = {
                    // BOTÓN PARA VOLVERatrás
                    TextButton(onClick = onNavigateBack) {
                        Text("< Atrás", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6750A4))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMsg != null -> Text(errorMsg!!, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                ventas.isEmpty() -> Text("No tienes compras registradas", modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(ventas) { venta ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (venta.resultado) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Venta #${venta.ventaId}", style = MaterialTheme.typography.titleMedium)
                                    Text("Fecha: ${venta.fechaVenta.take(10)}")
                                    Text("Asientos: ${venta.cantidadAsientos}")
                                    Text("Total: $${venta.precioVenta}", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = venta.descripcion,
                                        color = if (venta.resultado) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}