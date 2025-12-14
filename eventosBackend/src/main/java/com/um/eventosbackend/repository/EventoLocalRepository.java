package com.um.eventosbackend.repository;

import com.um.eventosbackend.domain.EventoLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventoLocalRepository extends JpaRepository<EventoLocal, Long> {

    Optional<EventoLocal> findByIdCatedra(Long idCatedra);

    List<EventoLocal> findByActivoTrueOrderByFechaAsc();

    Optional<EventoLocal> findByIdAndActivoTrue(Long id);
}
