package com.um.eventosmovil.data

import kotlinx.serialization.Serializable

@Serializable
data class EventoDTO(
    val id: Long,
    val titulo: String,
    val resumen: String,
    val fecha: String,
    val imagen: String? = null
)
@Serializable
data class EventoDetalleDTO(
    val id: Long,
    val titulo: String,
    val resumen: String,
    val descripcion: String,
    val fecha: String,
    val direccion: String,
    val imagen: String? = null,
    val filasAsientos: Int,
    val columnasAsientos: Int,
    val precioEntrada: Double,
    val tipoNombre: String,
    val tipoDescripcion: String
)