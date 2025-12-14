package com.um.eventosbackend.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(
    name = "integrante_local",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_integrante_local", columnNames = {"nombre", "apellido", "identificacion"})
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IntegranteLocal extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "identificacion")
    private String identificacion;

    // lado inverso (opcional, pero ayuda para “clásico JPA/JHipster”)
    @ManyToMany(mappedBy = "integrantes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<EventoLocal> eventos = new HashSet<>();

    @Override
    public Long getId() {
        return this.id;
    }

    public IntegranteLocal id(Long id) {
        this.id = id;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public IntegranteLocal nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public IntegranteLocal apellido(String apellido) {
        this.apellido = apellido;
        return this;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public IntegranteLocal identificacion(String identificacion) {
        this.identificacion = identificacion;
        return this;
    }

    public Set<EventoLocal> getEventos() {
        return eventos;
    }

    public void setEventos(Set<EventoLocal> eventos) {
        this.eventos = eventos;
    }
}
