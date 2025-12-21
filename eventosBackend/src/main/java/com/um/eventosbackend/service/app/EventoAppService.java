package com.um.eventosbackend.service.app;

import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.repository.EventoLocalRepository;
import com.um.eventosbackend.service.dto.app.EventoDetalleDTO;
import com.um.eventosbackend.service.dto.app.EventoResumenDTO;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class EventoAppService {

    private final EventoLocalRepository eventoLocalRepository;

    public EventoAppService(EventoLocalRepository eventoLocalRepository) {
        this.eventoLocalRepository = eventoLocalRepository;
    }

    public List<EventoResumenDTO> listarEventos() { // Lista de eventos activos
        return eventoLocalRepository.findByActivoTrueOrderByFechaAsc().stream()
            .map(this::toResumenDTO)
            .collect(Collectors.toList());
    }

    public Optional<EventoDetalleDTO> obtenerEvento(Long id) {
        Optional<EventoLocal> evento = eventoLocalRepository.findByIdCatedraAndActivoTrue(id);

        if (evento.isEmpty()) {
            evento = eventoLocalRepository.findByIdAndActivoTrue(id);
        }

        return evento.map(this::toDetalleDTO);
    }

    private EventoResumenDTO toResumenDTO(EventoLocal e) {
        EventoResumenDTO dto = new EventoResumenDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setResumen(e.getResumen());
        dto.setFecha(e.getFecha());
        dto.setImagen(e.getImagen());
        return dto;
    }

    private EventoDetalleDTO toDetalleDTO(EventoLocal e) {
        EventoDetalleDTO dto = new EventoDetalleDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setResumen(e.getResumen());
        dto.setDescripcion(e.getDescripcion());
        dto.setFecha(e.getFecha());
        dto.setDireccion(e.getDireccion());
        dto.setImagen(e.getImagen());
        dto.setFilasAsientos(e.getFilasAsientos());
        dto.setColumnasAsientos(e.getColumnasAsientos());
        dto.setPrecioEntrada(e.getPrecioEntrada());

        if (e.getEventoTipo() != null) {
            dto.setTipoNombre(e.getEventoTipo().getNombre());
            dto.setTipoDescripcion(e.getEventoTipo().getDescripcion());
        }

        return dto;
    }
}
