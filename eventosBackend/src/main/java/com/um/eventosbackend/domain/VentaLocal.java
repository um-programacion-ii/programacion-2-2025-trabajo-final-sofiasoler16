package com.um.eventosbackend.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "venta_local")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class VentaLocal extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Estado {
        PENDIENTE,
        CONFIRMADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado;

    /** Id de la venta en el servicio de la cátedra (si ya se confirmó allá). */
    @Column(name = "id_catedra")
    private Long idCatedra;

    @Column(name = "monto_total", precision = 21, scale = 2)
    private BigDecimal montoTotal;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoLocal evento;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<AsientoVenta> asientos = new HashSet<>();

    @Override
    public Long getId() {
        return this.id;
    }

    public VentaLocal id(Long id) {
        this.id = id;
        return this;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public VentaLocal estado(Estado estado) {
        this.estado = estado;
        return this;
    }

    public Long getIdCatedra() {
        return idCatedra;
    }

    public void setIdCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
    }

    public VentaLocal idCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
        return this;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public VentaLocal montoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
        return this;
    }

    public EventoLocal getEvento() {
        return evento;
    }

    public void setEvento(EventoLocal evento) {
        this.evento = evento;
    }

    public VentaLocal evento(EventoLocal evento) {
        this.evento = evento;
        return this;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public VentaLocal usuario(User usuario) {
        this.usuario = usuario;
        return this;
    }

    public Set<AsientoVenta> getAsientos() {
        return asientos;
    }

    public void setAsientos(Set<AsientoVenta> asientos) {
        this.asientos = asientos;
    }

    public VentaLocal addAsiento(AsientoVenta asientoVenta) {
        this.asientos.add(asientoVenta);
        asientoVenta.setVenta(this);
        return this;
    }

    public VentaLocal removeAsiento(AsientoVenta asientoVenta) {
        this.asientos.remove(asientoVenta);
        asientoVenta.setVenta(null);
        return this;
    }
}
