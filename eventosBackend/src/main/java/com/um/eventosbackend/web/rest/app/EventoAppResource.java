package com.um.eventosbackend.web.rest.app;

import com.um.eventosbackend.service.app.EventoAppService;
import com.um.eventosbackend.service.catedra.EventoSyncService;
import com.um.eventosbackend.service.dto.app.EventoDetalleDTO;
import com.um.eventosbackend.service.dto.app.EventoResumenDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/app/eventos")
public class EventoAppResource {

    private final EventoAppService eventoAppService;
    private final EventoSyncService eventoSyncService;

    public EventoAppResource(EventoAppService eventoAppService, EventoSyncService eventoSyncService) {
        this.eventoAppService = eventoAppService;
        this.eventoSyncService = eventoSyncService;
    }


    @GetMapping
    public List<EventoResumenDTO> listar() {
        return eventoAppService.listarEventos();
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventoDetalleDTO> obtener(@PathVariable Long id) {
        return eventoAppService.obtenerPorIdCatedra(id)
            .or(() -> eventoAppService.obtenerEvento(id))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> dispararSincronizacion() {
        eventoSyncService.syncEventos();
        return ResponseEntity.ok().build();
    }

    // En EventoAppResource.java
    @GetMapping("/{id}/asientos")
    public ResponseEntity<Object> obtenerAsientos(
        @PathVariable Long id,
        @RequestParam Integer filas,
        @RequestParam Integer columnas
    ) {
        // Le pedimos al service que busque la matriz en el proxy
        return ResponseEntity.ok(eventoAppService.obtenerMapaAsientos(id, filas, columnas));
    }
}
