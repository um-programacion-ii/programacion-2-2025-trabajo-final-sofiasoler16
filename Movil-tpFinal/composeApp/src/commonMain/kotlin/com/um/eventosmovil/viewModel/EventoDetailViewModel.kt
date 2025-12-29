package com.um.eventosmovil.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.um.eventosmovil.data.EventoDetalleDTO
import com.um.eventosmovil.service.EventoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EventDetailState {
    object Loading : EventDetailState()
    data class Success(val evento: EventoDetalleDTO) : EventDetailState()
    data class Error(val message: String) : EventDetailState()
}

class EventDetailViewModel(private val service: EventoService, private val eventId: Long) : ViewModel() {
    private val _state = MutableStateFlow<EventDetailState>(EventDetailState.Loading)
    val state: StateFlow<EventDetailState> = _state

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            service.getEventoDetalle(eventId)
                .onSuccess { _state.value = EventDetailState.Success(it) }
                .onFailure { _state.value = EventDetailState.Error(it.message ?: "Error desconocido") }
        }
    }
}