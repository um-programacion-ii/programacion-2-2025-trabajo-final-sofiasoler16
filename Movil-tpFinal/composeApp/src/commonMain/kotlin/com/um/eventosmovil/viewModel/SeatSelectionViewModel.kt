package com.um.eventosmovil.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.um.eventosmovil.data.AsientoPosicion
import com.um.eventosmovil.data.MapaAsientosResponse
import com.um.eventosmovil.service.EventoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SeatState {
    object Loading : SeatState()
    data class Success(val mapa: MapaAsientosResponse, val seleccionados: Set<AsientoPosicion>) : SeatState()
    data class Error(val message: String) : SeatState()
}

class SeatSelectionViewModel(private val service: EventoService, private val eventId: Long) : ViewModel() {
    private val _state = MutableStateFlow<SeatState>(SeatState.Loading)
    val state: StateFlow<SeatState> = _state

    private var seleccionados = mutableSetOf<AsientoPosicion>()

    init { loadMapa() }

    fun loadMapa() {
        viewModelScope.launch {
            _state.value = SeatState.Loading
            // Usamos dimensiones estándar para la consulta inicial [cite: 2025-11-11]
            service.getMapaAsientos(eventId, 10, 10).onSuccess { mapa ->
                _state.value = SeatState.Success(mapa, seleccionados.toSet())
            }.onFailure {
                _state.value = SeatState.Error("No se pudo cargar el mapa")
            }
        }
    }

    fun toggleSeat(fila: Int, columna: Int) {
        val pos = AsientoPosicion(fila, columna)
        if (seleccionados.contains(pos)) {
            seleccionados.remove(pos)
        } else if (seleccionados.size < 4) { // Límite del Issue #30
            seleccionados.add(pos)
        }

        // Actualizamos el estado para que la UI se repinte
        val currentState = _state.value
        if (currentState is SeatState.Success) {
            _state.value = currentState.copy(seleccionados = seleccionados.toSet())
        }
    }

    fun confirmarSeleccion(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            // Llamamos al endpoint de bloqueo que definiste en SesionService
            service.bloquearAsientos(eventId, seleccionados.toList()).onSuccess {
                onSuccess() // Si el backend bloqueó bien, vamos a la carga de nombres
            }.onFailure { exception ->
                onError(exception.message ?: "Error al conectar con el servidor")
            }
        }
    }
}