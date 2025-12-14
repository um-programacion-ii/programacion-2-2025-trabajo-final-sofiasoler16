package com.um.eventosbackend.service.catedra;

import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.repository.EventoLocalRepository;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoDetalleDTO;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


@Service
@Transactional
public class EventoSyncService {

    private static final Logger log = LoggerFactory.getLogger(EventoSyncService.class);

    private final CatedraClient catedraClient;
    private final EventoLocalRepository eventoLocalRepository;

    @Value("${app.sync.catedra.delete-missing:true}")
    private boolean deleteMissing = true;


    public EventoSyncService(CatedraClient catedraClient, EventoLocalRepository eventoLocalRepository) {
        this.catedraClient = catedraClient;
        this.eventoLocalRepository = eventoLocalRepository;
    }

    public void syncEventos() {
        log.info("Iniciando sincronización de eventos con cátedra...");

        List<CatedraEventoDetalleDTO> eventosRemotos;
        try {
            eventosRemotos = catedraClient.listarEventos();
        } catch (RuntimeException ex) {
            // Por si el servicio de cátedra está caído o no accesible
            log.warn("No se pudo obtener el listado de eventos de la cátedra", ex);
            return;
        }

        Map<Long, CatedraEventoDetalleDTO> remotosPorId = eventosRemotos.stream()
            .collect(Collectors.toMap(CatedraEventoDetalleDTO::getId, Function.identity()));

        List<EventoLocal> eventosLocales = eventoLocalRepository.findAll();

        // --- Altas y modificaciones ---
        for (CatedraEventoDetalleDTO remoto : eventosRemotos) {
            Long idCatedra = remoto.getId();
            Optional<EventoLocal> existenteOpt = eventoLocalRepository.findByIdCatedra(idCatedra);

            EventoLocal eventoLocal = existenteOpt.orElseGet(EventoLocal::new);

            boolean esNuevo = eventoLocal.getId() == null;

            mapearDesdeCatedra(remoto, eventoLocal);

            if (esNuevo) {
                log.info("Creando evento_local nuevo para idCatedra={}", idCatedra);
            } else {
                log.info("Actualizando evento_local existente id={} (idCatedra={})", eventoLocal.getId(), idCatedra);
            }

            eventoLocalRepository.save(eventoLocal);
        }

        // --- Bajas ---
        if (deleteMissing) {
            Set<Long> idsRemotos = remotosPorId.keySet();

            for (EventoLocal local : eventosLocales) {
                Long idCatedra = local.getIdCatedra();
                if (idCatedra != null && !idsRemotos.contains(idCatedra)) {
                    log.info(
                        "Eliminando evento_local id={} (idCatedra={}) porque ya no existe en cátedra",
                        local.getId(),
                        idCatedra
                    );
                    eventoLocalRepository.delete(local);
                }
            }
        } else {
            log.debug("Bajas deshabilitadas (app.sync.catedra.delete-missing=false)");
        }

        log.info("Sincronización de eventos con cátedra finalizada");


    }

    private void mapearDesdeCatedra(CatedraEventoDetalleDTO remoto, EventoLocal local) {
        local.setIdCatedra(remoto.getId());
        local.setTitulo(remoto.getTitulo());
        local.setResumen(remoto.getResumen());
        local.setDescripcion(remoto.getDescripcion());
        local.setFecha(remoto.getFecha());
        local.setDireccion(remoto.getDireccion());
        local.setImagen(remoto.getImagen());
        local.setFilasAsientos(remoto.getFilaAsientos());
        local.setColumnasAsientos(remoto.getColumnAsientos());
        local.setPrecioEntrada(remoto.getPrecioEntrada());

        if (remoto.getEventoTipo() != null) {
            local.setTipoNombre(remoto.getEventoTipo().getNombre());
            local.setTipoDescripcion(remoto.getEventoTipo().getDescripcion());
        } else {
            local.setTipoNombre(null);
            local.setTipoDescripcion(null);
        }
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 300000)
    public void syncPeriodicamente() {
        syncEventos();
    }
}
