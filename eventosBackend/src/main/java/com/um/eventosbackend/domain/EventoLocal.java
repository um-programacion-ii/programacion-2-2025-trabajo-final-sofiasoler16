package com.um.eventosbackend.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.Instant;

@Entity
@Table(name = "evento_local")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EventoLocal extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /** Id del evento en el servicio de la cátedra */
    @Column(name = "id_catedra", nullable = false)
    private Long idCatedra;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "resumen")
    private String resumen;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    private Instant fecha;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "imagen")
    private String imagen;

    @Column(name = "filas_asientos")
    private Integer filasAsientos;

    @Column(name = "columnas_asientos")
    private Integer columnasAsientos;

    @Column(name = "precio_entrada", precision = 21, scale = 2)
    private BigDecimal precioEntrada;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_expiracion")
    private Instant fechaExpiracion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_tipo_id", nullable = false)
    private EventoTipoLocal eventoTipo;

    @ManyToMany
    @JoinTable(
        name = "rel_evento_local__integrante_local",
        joinColumns = @JoinColumn(name = "evento_local_id"),
        inverseJoinColumns = @JoinColumn(name = "integrante_local_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<IntegranteLocal> integrantes = new HashSet<>();

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<VentaLocal> ventas = new HashSet<>();

    @Override
    public Long getId() {
        return this.id;
    }

    public EventoLocal id(Long id) {
        this.id = id;
        return this;
    }

    public Long getIdCatedra() {
        return idCatedra;
    }

    public void setIdCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
    }

    public EventoLocal idCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public EventoLocal titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public EventoLocal resumen(String resumen) {
        this.resumen = resumen;
        return this;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EventoLocal descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public EventoLocal fecha(Instant fecha) {
        this.fecha = fecha;
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public EventoLocal direccion(String direccion) {
        this.direccion = direccion;
        return this;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public EventoLocal imagen(String imagen) {
        this.imagen = imagen;
        return this;
    }

    public Integer getFilasAsientos() {
        return filasAsientos;
    }

    public void setFilasAsientos(Integer filasAsientos) {
        this.filasAsientos = filasAsientos;
    }

    public EventoLocal filasAsientos(Integer filasAsientos) {
        this.filasAsientos = filasAsientos;
        return this;
    }

    public Integer getColumnasAsientos() {
        return columnasAsientos;
    }

    public void setColumnasAsientos(Integer columnasAsientos) {
        this.columnasAsientos = columnasAsientos;
    }

    public EventoLocal columnasAsientos(Integer columnasAsientos) {
        this.columnasAsientos = columnasAsientos;
        return this;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public EventoLocal precioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
        return this;
    }

    public Set<VentaLocal> getVentas() {
        return ventas;
    }

    public void setVentas(Set<VentaLocal> ventas) {
        this.ventas = ventas;
    }

    public EventoLocal addVenta(VentaLocal ventaLocal) {
        this.ventas.add(ventaLocal);
        ventaLocal.setEvento(this);
        return this;
    }

    public EventoLocal removeVenta(VentaLocal ventaLocal) {
        this.ventas.remove(ventaLocal);
        ventaLocal.setEvento(null);
        return this;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public EventoLocal activo(Boolean activo) {
        this.activo = activo;
        return this;
    }

    public Instant getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Instant fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public EventoLocal fechaExpiracion(Instant fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
        return this;
    }
    public EventoTipoLocal getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(EventoTipoLocal eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public EventoLocal eventoTipo(EventoTipoLocal eventoTipo) {
        this.eventoTipo = eventoTipo;
        return this;
    }

    public Set<IntegranteLocal> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(Set<IntegranteLocal> integrantes) {
        this.integrantes = integrantes;
    }

    public EventoLocal integrantes(Set<IntegranteLocal> integrantes) {
        this.integrantes = integrantes;
        return this;
    }

    public EventoLocal addIntegrante(IntegranteLocal integranteLocal) {
        this.integrantes.add(integranteLocal);
        return this;
    }

    public EventoLocal removeIntegrante(IntegranteLocal integranteLocal) {
        this.integrantes.remove(integranteLocal);
        return this;
    }


}
