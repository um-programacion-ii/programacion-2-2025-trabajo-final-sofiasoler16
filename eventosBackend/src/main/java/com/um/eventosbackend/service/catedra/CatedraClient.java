package com.um.eventosbackend.service.catedra;

import com.um.eventosbackend.service.dto.catedra.CatedraEventoDetalleDTO;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoResumenDTO;

import java.util.List;

public interface CatedraClient {

    List<CatedraEventoResumenDTO> listarEventosResumidos();

    List<CatedraEventoDetalleDTO> listarEventos();

    CatedraEventoDetalleDTO obtenerEventoPorId(Long id);
}
