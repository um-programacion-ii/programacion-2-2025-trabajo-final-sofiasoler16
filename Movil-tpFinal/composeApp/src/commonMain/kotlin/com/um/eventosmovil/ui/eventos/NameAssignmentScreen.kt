package com.um.eventosmovil.ui.eventos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.um.eventosmovil.data.AsientoDTO
import com.um.eventosmovil.data.AsientoPosicion



@Composable
fun NameAssignmentScreen(
    asientos: List<AsientoPosicion>,
    onConfirmPurchase: (List<AsientoDTO>) -> Unit
) {
    // Estado para guardar los nombres que el usuario escribe
    val datosAsientos = remember {
        mutableStateMapOf<AsientoPosicion, Pair<String, String>>()
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Carga de nombres", style = MaterialTheme.typography.headlineMedium)

        asientos.forEach { asiento ->
            Card(modifier = Modifier.padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Asiento: Fila ${asiento.fila}, Col ${asiento.columna}")
                    TextField(
                        value = datosAsientos[asiento]?.first ?: "",
                        onValueChange = { nombre ->
                            datosAsientos[asiento] = nombre to (datosAsientos[asiento]?.second ?: "")
                        },
                        label = { Text("Nombre") }
                    )
                    TextField(
                        value = datosAsientos[asiento]?.second ?: "",
                        onValueChange = { apellido ->
                            datosAsientos[asiento] = (datosAsientos[asiento]?.first ?: "") to apellido
                        },
                        label = { Text("Apellido") }
                    )
                }
            }
        }

        Button(
            onClick = {
                // Convertimos el mapa a una lista de DTOs para enviar al POST /api/app/venta
                val listaFinal = asientos.map {
                    AsientoDTO(it.fila, it.columna, datosAsientos[it]?.first, datosAsientos[it]?.second)
                }
                onConfirmPurchase(listaFinal)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Comprar")
        }
    }
}