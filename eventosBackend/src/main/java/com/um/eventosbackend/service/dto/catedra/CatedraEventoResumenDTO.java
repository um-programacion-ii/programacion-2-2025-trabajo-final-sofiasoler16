package com.um.eventosbackend.service.dto.catedra;

import java.math.BigDecimal;
import java.time.Instant;

public class CatedraEventoResumenDTO {

    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private Instant fecha;
    private BigDecimal precioEntrada;
    private CatedraEventoTipoDTO eventoTipo;

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
}
