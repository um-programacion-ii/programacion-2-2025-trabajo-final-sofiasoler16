package com.um.eventosbackend.service.dto.app;

import java.io.Serializable;

public class AsientoSeleccionadoDTO implements Serializable {
    private Integer fila;
    private Integer columna;
    private String nombrePersona;

    public AsientoSeleccionadoDTO() {}

    public AsientoSeleccionadoDTO(Integer fila, Integer columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }
    public Integer getColumna() { return columna; }
    public void setColumna(Integer columna) { this.columna = columna; }
    public String getNombrePersona() { return nombrePersona; }
    public void setNombrePersona(String nombrePersona) { this.nombrePersona = nombrePersona; }
}
