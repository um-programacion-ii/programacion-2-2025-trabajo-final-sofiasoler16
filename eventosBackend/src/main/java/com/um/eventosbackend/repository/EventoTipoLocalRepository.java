package com.um.eventosbackend.repository;

import com.um.eventosbackend.domain.EventoTipoLocal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoTipoLocalRepository extends JpaRepository<EventoTipoLocal, Long> {
    Optional<EventoTipoLocal> findOneByNombre(String nombre);
}
