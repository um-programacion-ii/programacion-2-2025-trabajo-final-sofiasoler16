package com.um.eventosbackend.service.dto.app;

public class AsignarNombreRequest {
    private Integer fila;
    private Integer columna;
    private String nombre;
    private String apellido;

    // Getters y Setters
    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }
    public Integer getColumna() { return columna; }
    public void setColumna(Integer columna) { this.columna = columna; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
