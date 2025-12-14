package com.um.eventosbackend.repository;

import com.um.eventosbackend.domain.IntegranteLocal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegranteLocalRepository extends JpaRepository<IntegranteLocal, Long> {
    Optional<IntegranteLocal> findOneByNombreAndApellidoAndIdentificacion(String nombre, String apellido, String identificacion);
}
