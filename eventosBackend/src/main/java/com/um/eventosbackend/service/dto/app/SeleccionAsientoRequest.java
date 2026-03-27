package com.um.eventosbackend.service.dto.app;

public class SeleccionAsientoRequest {
    private Integer fila;
    private Integer columna;

    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }
    public Integer getColumna() { return columna; }
    public void setColumna(Integer columna) { this.columna = columna; }
}
