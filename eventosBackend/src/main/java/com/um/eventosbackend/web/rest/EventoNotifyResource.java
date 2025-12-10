package com.um.eventosbackend.web.rest;

import com.um.eventosbackend.service.catedra.EventoSyncService;
import com.um.eventosbackend.service.dto.proxy.EventoNotifyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EventoNotifyResource {

    private static final Logger log = LoggerFactory.getLogger(EventoNotifyResource.class);

    private final EventoSyncService eventoSyncService;

    public EventoNotifyResource(EventoSyncService eventoSyncService) {
        this.eventoSyncService = eventoSyncService;
    }

 // {@code POST  /admin/eventos/notify} : recibe notificaciones de cambios de eventos.

    @PostMapping("/admin/eventos/notify")
    public ResponseEntity<Void> notifyCambioEvento(@RequestBody(required = false) EventoNotifyDTO notification) {
        if (notification != null && notification.getIdCatedra() != null) {
            log.info("Recibida notificación de cambio de evento desde proxy. idCatedra={}", notification.getIdCatedra());
        } else {
            log.info("Recibida notificación genérica de cambios de eventos desde proxy.");
        }

        eventoSyncService.syncEventos();

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
