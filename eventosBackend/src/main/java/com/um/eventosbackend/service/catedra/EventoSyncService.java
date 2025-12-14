package com.um.eventosbackend.service.catedra;

import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.domain.EventoTipoLocal;
import com.um.eventosbackend.domain.IntegranteLocal;
import com.um.eventosbackend.repository.EventoLocalRepository;
import com.um.eventosbackend.repository.EventoTipoLocalRepository;
import com.um.eventosbackend.repository.IntegranteLocalRepository;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoDetalleDTO;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoIntegranteDTO;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventoSyncService {

    private static final Logger log = LoggerFactory.getLogger(EventoSyncService.class);

    private final CatedraClient catedraClient;
    private final EventoLocalRepository eventoLocalRepository;
    private final EventoTipoLocalRepository eventoTipoLocalRepository;
    private final IntegranteLocalRepository integranteLocalRepository;

    @Value("${app.sync.catedra.delete-missing:true}")
    private boolean deleteMissing = true;

    public EventoSyncService(
        CatedraClient catedraClient,
        EventoLocalRepository eventoLocalRepository,
        EventoTipoLocalRepository eventoTipoLocalRepository,
        IntegranteLocalRepository integranteLocalRepository
    ) {
        this.catedraClient = catedraClient;
        this.eventoLocalRepository = eventoLocalRepository;
        this.eventoTipoLocalRepository = eventoTipoLocalRepository;
        this.integranteLocalRepository = integranteLocalRepository;
    }

    public void syncEventos() {
        log.info("Iniciando sincronización de eventos con cátedra...");

        List<CatedraEventoDetalleDTO> eventosRemotos;
        try {
            eventosRemotos = catedraClient.listarEventos();
        } catch (RuntimeException ex) {
            log.warn("No se pudo obtener el listado de eventos de la cátedra", ex);
            return;
        }

        Map<Long, CatedraEventoDetalleDTO> remotosPorId = eventosRemotos.stream()
            .collect(Collectors.toMap(CatedraEventoDetalleDTO::getId, Function.identity()));

        List<EventoLocal> eventosLocales = eventoLocalRepository.findAll();

        // Altas y modificaciones
        for (CatedraEventoDetalleDTO remoto : eventosRemotos) {
            Long idCatedra = remoto.getId();

            EventoLocal eventoLocal = eventoLocalRepository
                .findByIdCatedra(idCatedra)
                .orElseGet(EventoLocal::new);

            boolean esNuevo = eventoLocal.getId() == null;

            mapearDesdeCatedra(remoto, eventoLocal);

            if (esNuevo) {
                log.info("Creando evento_local nuevo para idCatedra={}", idCatedra);
            } else {
                log.info("Actualizando evento_local existente id={} (idCatedra={})", eventoLocal.getId(), idCatedra);
            }

            eventoLocalRepository.save(eventoLocal);
        }

        // Bajas
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

        // -------- EventoTipoLocal (ManyToOne) --------
        if (remoto.getEventoTipo() == null || remoto.getEventoTipo().getNombre() == null) {
            // como tu columna evento_tipo_id es nullable=false
            throw new IllegalStateException("Evento cátedra id=" + remoto.getId() + " vino sin eventoTipo");
        }

        String nombreTipo = remoto.getEventoTipo().getNombre();
        String descTipo = remoto.getEventoTipo().getDescripcion();

        EventoTipoLocal tipo = eventoTipoLocalRepository
            .findOneByNombre(nombreTipo)
            .orElseGet(() -> {
                EventoTipoLocal nuevo = new EventoTipoLocal();
                nuevo.setNombre(nombreTipo);
                nuevo.setDescripcion(descTipo);
                return eventoTipoLocalRepository.save(nuevo);
            });

        // si existe y cambia la descripción, la actualizamos
        if (descTipo != null && (tipo.getDescripcion() == null || !descTipo.equals(tipo.getDescripcion()))) {
            tipo.setDescripcion(descTipo);
            tipo = eventoTipoLocalRepository.save(tipo);
        }

        local.setEventoTipo(tipo);

        // -------- IntegrantesLocal (ManyToMany) --------
        Set<IntegranteLocal> nuevos = new HashSet<>();
        if (remoto.getIntegrantes() != null) {
            for (CatedraEventoIntegranteDTO i : remoto.getIntegrantes()) {
                String nombre = i.getNombre();
                String apellido = i.getApellido();
                String identificacion = i.getIdentificacion();

                IntegranteLocal integrante = integranteLocalRepository
                    .findOneByNombreAndApellidoAndIdentificacion(nombre, apellido, identificacion)
                    .orElseGet(() -> {
                        IntegranteLocal n = new IntegranteLocal();
                        n.setNombre(nombre);
                        n.setApellido(apellido);
                        n.setIdentificacion(identificacion);
                        return integranteLocalRepository.save(n);
                    });

                nuevos.add(integrante);
            }
        }

        // mantener colección persistente
        local.getIntegrantes().clear();
        local.getIntegrantes().addAll(nuevos);
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 300000)
    public void syncPeriodicamente() {
        syncEventos();
    }
}
