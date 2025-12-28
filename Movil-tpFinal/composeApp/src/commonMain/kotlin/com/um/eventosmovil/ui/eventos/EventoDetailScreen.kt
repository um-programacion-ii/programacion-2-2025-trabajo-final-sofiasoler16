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
import com.um.eventosmovil.data.EventoDTO
import movil_tpfinal.composeapp.generated.resources.Res
import movil_tpfinal.composeapp.generated.resources.fondo2
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetailScreen(
    evento: EventoDTO,
    onNavigateBack: () -> Unit
) {
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
                    title = { Text("Detalle del Evento", color = Color.White) },
                    navigationIcon = {
                        TextButton(onClick = onNavigateBack) {
                            Text("< Volver", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6750A4))
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(text = evento.titulo, style = MaterialTheme.typography.headlineMedium, color = Color(0xFF4A148C))
                        Text(text = "Fecha: ${evento.fecha}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Descripción", style = MaterialTheme.typography.titleMedium)
                        Text(text = evento.resumen, style = MaterialTheme.typography.bodyLarge) // Aquí iría la información completa

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { /* Próximo issue: Selección de asientos */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                        ) {
                            Text("Ver asientos") // Requerimiento del issue
                        }
                    }
                }
            }
        }
    }
}