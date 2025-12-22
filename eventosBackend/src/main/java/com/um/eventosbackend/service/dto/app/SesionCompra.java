package com.um.eventosbackend.service.dto.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SesionCompra implements Serializable {
    private String usuario;
    private Long eventoId;
    private String etapaActual;
    private List<AsientoSeleccionadoDTO> asientos = new ArrayList<>(); // Máximo 4 [cite: 164]

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public String getEtapaActual() { return etapaActual; }
    public void setEtapaActual(String etapaActual) { this.etapaActual = etapaActual; }
    public List<AsientoSeleccionadoDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoSeleccionadoDTO> asientos) { this.asientos = asientos; }
}
