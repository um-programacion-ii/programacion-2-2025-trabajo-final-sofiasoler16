package com.um.eventosmovil.data

import kotlinx.serialization.Serializable

@Serializable
data class EventoDTO(
    val id: Long,
    val titulo: String,
    val fecha: String,
    val resumen: String,
    val imagen: String? = null // Opcional, por si quieres mostrar la imagen después
)