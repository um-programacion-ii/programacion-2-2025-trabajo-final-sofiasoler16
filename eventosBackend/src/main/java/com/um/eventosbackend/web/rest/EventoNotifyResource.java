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

    @PostMapping("/public/eventos/notify")
    public ResponseEntity<Void> notifyCambioEvento(@RequestBody(required = false) EventoNotifyDTO notification) {
        // Este log es tu mejor amigo para saber que se está usando este archivo
        if (notification != null && notification.getIdCatedra() != null) {
            log.info("📢 [NOTIFICACIÓN] Sincronizando evento específico ID: {}", notification.getIdCatedra());
        } else {
            log.info("📢 [NOTIFICACIÓN] Sincronización general activada.");
        }

        eventoSyncService.syncEventos();

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
