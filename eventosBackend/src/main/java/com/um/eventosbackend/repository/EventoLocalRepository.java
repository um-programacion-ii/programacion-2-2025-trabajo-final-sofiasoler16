package com.um.eventosbackend.repository;

import com.um.eventosbackend.domain.EventoLocal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoLocalRepository extends JpaRepository<EventoLocal, Long> {

    Optional<EventoLocal> findByIdCatedra(Long idCatedra);

    @EntityGraph(attributePaths = {"eventoTipo", "integrantes"})
    List<EventoLocal> findByActivoTrueOrderByFechaAsc();

    // Cambiamos el EntityGraph por una Query con JOIN FETCH para forzar la carga
    @Query("select e from EventoLocal e " +
        "left join fetch e.eventoTipo " +
        "left join fetch e.integrantes " +
        "where e.id = :id and e.activo = true")
    Optional<EventoLocal> findByIdAndActivoTrue(@Param("id") Long id);

    @Query("select e from EventoLocal e " +
        "left join fetch e.eventoTipo " +
        "left join fetch e.integrantes " +
        "where e.idCatedra = :idCatedra and e.activo = true")
    Optional<EventoLocal> findByIdCatedraAndActivoTrue(@Param("idCatedra") Long idCatedra);
}
