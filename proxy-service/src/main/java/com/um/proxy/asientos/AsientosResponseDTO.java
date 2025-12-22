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

    public Long getEventoId() { return eventoId; }
    public Integer getFilas() { return filas; }
    public Integer getColumnas() { return columnas; }
    public List<List<AsientoEstado>> getMatriz() { return matriz; }


    // Esta es la clase que le falta a tu proyecto y causa los errores en rojo
    public static class AsientoDTO {
        private Integer fila;
        private Integer columna;
        private String estado;
        private String expira;

        public AsientoDTO() {}

        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }

        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public String getExpira() { return expira; }
        public void setExpira(String expira) { this.expira = expira; }
    }
}