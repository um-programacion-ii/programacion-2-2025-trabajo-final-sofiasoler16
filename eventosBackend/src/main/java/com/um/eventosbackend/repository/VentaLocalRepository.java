package com.um.eventosbackend.repository;

import com.um.eventosbackend.domain.VentaLocal;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaLocalRepository extends JpaRepository<VentaLocal, Long> {

    // List<VentaLocal> findByUsuarioLogin(String login); // ES necesario??

    @EntityGraph(attributePaths = { "asientos", "evento" })
    List<VentaLocal> findAllByEstado(VentaLocal.Estado estado);
}
