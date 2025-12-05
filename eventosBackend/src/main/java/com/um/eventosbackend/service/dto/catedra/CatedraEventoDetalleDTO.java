package com.um.eventosbackend.service.dto.catedra;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CatedraEventoDetalleDTO {

    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private Instant fecha;

    private String direccion;
    private String imagen;

    private int filaAsientos;
    private int columnAsientos;

    private BigDecimal precioEntrada;
    private CatedraEventoTipoDTO eventoTipo;
    private List<CatedraEventoIntegranteDTO> integrantes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(int filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public int getColumnAsientos() {
        return columnAsientos;
    }

    public void setColumnAsientos(int columnAsientos) {
        this.columnAsientos = columnAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public CatedraEventoTipoDTO getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(CatedraEventoTipoDTO eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public List<CatedraEventoIntegranteDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<CatedraEventoIntegranteDTO> integrantes) {
        this.integrantes = integrantes;
    }
}
