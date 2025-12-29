package com.um.eventosbackend.web.rest.app;


import com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO;

import java.util.List;

class BloqueoRequestResource {
    private Long eventoId;
    private List<com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO> asientos;

    // Getters y Setters
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoSeleccionadoDTO> getAsientos() { return asientos; }
    public void setAsientos(List<com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO> asientos) { this.asientos = asientos; }
}
