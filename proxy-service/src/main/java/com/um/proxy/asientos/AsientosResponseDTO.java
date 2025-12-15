package com.um.proxy.asientos;

import java.util.List;

public class AsientosResponseDTO {

    private Long eventoId;
    private Integer filas;
    private Integer columnas;
    private List<List<AsientoEstado>> matriz;

    public AsientosResponseDTO(Long eventoId, Integer filas, Integer columnas, List<List<AsientoEstado>> matriz) {
        this.eventoId = eventoId;
        this.filas = filas;
        this.columnas = columnas;
        this.matriz = matriz;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public Integer getFilas() {
        return filas;
    }

    public Integer getColumnas() {
        return columnas;
    }

    public List<List<AsientoEstado>> getMatriz() {
        return matriz;
    }
}
