package com.um.proxy.service.dto;

import java.util.List;

public class CatedraAsientosDTO {
    private Long eventoId;
    private List<AsientoDTO> asientos;

    public CatedraAsientosDTO() {}

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public List<AsientoDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoDTO> asientos) { this.asientos = asientos; }

    public static class AsientoDTO {
        private Integer fila;
        private Integer columna;
        private String estado;

        public AsientoDTO() {}

        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}