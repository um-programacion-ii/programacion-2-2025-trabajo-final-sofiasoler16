package com.um.eventosbackend.service.evento;

import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.repository.EventoLocalRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventoEstadoService {

    private static final Logger log = LoggerFactory.getLogger(EventoEstadoService.class);

    private final EventoLocalRepository eventoLocalRepository;

    public EventoEstadoService(EventoLocalRepository eventoLocalRepository) {
        this.eventoLocalRepository = eventoLocalRepository;
    }

    public void actualizarEstadosEventos() {
        Instant ahora = Instant.now();

        List<EventoLocal> eventos = eventoLocalRepository.findAll();

        for (EventoLocal evento : eventos) {
            if (evento.getFecha() == null) {
                continue;
            }

            if (evento.getFecha().isBefore(ahora)) {
                // Expirado
                if (Boolean.TRUE.equals(evento.getActivo())) {
                    log.info("Marcando evento {} como EXPIRADO", evento.getId());
                }
                evento.setActivo(false);
                if (evento.getFechaExpiracion() == null) {
                    evento.setFechaExpiracion(evento.getFecha());
                }
            } else {
                // Sigue activo
                if (!Boolean.TRUE.equals(evento.getActivo())) {
                    log.info("Marcando evento {} como ACTIVO nuevamente", evento.getId());
                }
                evento.setActivo(true);
                // Usamos la fecha del evento como fecha "teórica" de expiración
                evento.setFechaExpiracion(evento.getFecha());
            }
        }

        eventoLocalRepository.saveAll(eventos);
    }

    @Scheduled(fixedDelay = 300000) // corre cada 5 minutos y recalcula estados
    public void actualizarPeriodicamente() {
        log.debug("Ejecutando job de actualización de estados de eventos...");
        actualizarEstadosEventos();
    }
}
