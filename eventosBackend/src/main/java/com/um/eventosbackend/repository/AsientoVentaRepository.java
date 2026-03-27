package com.um.eventosbackend.repository;

import com.um.eventosbackend.domain.AsientoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsientoVentaRepository extends JpaRepository<AsientoVenta, Long> {}
