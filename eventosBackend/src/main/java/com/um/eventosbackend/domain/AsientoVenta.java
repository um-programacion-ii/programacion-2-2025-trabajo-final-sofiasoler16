package com.um.eventosbackend.domain;
package com.um.eventosbackend.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "asiento_venta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AsientoVenta extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "fila")
    private Integer fila;

    @Column(name = "columna")
    private Integer columna;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaLocal venta;

    @Override
    public Long getId() {
        return this.id;
    }

    public AsientoVenta id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public AsientoVenta fila(Integer fila) {
        this.fila = fila;
        return this;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public AsientoVenta columna(Integer columna) {
        this.columna = columna;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public AsientoVenta nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public AsientoVenta apellido(String apellido) {
        this.apellido = apellido;
        return this;
    }

    public VentaLocal getVenta() {
        return venta;
    }

    public void setVenta(VentaLocal venta) {
        this.venta = venta;
    }

    public AsientoVenta venta(VentaLocal venta) {
        this.venta = venta;
        return this;
    }
}
