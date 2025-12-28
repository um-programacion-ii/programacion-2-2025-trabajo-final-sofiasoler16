package com.um.eventosmovil.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.service.EventoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EventListState {
    object Loading : EventListState()
    data class Success(val eventos: List<EventoDTO>) : EventListState()
    data class Error(val message: String) : EventListState()
}

class EventListViewModel(token: String) : ViewModel() {
    private val service = EventoService(token)
    private val _state = MutableStateFlow<EventListState>(EventListState.Loading)
    val state: StateFlow<EventListState> = _state

    init {
        loadEventos()
    }

    fun loadEventos() {
        viewModelScope.launch {
            _state.value = EventListState.Loading
            service.getEventos()
                .onSuccess { _state.value = EventListState.Success(it) }
                .onFailure { _state.value = EventListState.Error(it.message ?: "Error desconocido") }
        }
    }
}