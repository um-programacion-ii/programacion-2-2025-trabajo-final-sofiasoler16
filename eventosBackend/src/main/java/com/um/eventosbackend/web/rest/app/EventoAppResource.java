package com.um.eventosbackend.web.rest.app;

import com.um.eventosbackend.service.app.EventoAppService;
import com.um.eventosbackend.service.dto.app.EventoDetalleDTO;
import com.um.eventosbackend.service.dto.app.EventoResumenDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/app/eventos")
public class EventoAppResource {

    private final EventoAppService eventoAppService;

    public EventoAppResource(EventoAppService eventoAppService) {
        this.eventoAppService = eventoAppService;
    }


    @GetMapping
    public List<EventoResumenDTO> listar() {
        return eventoAppService.listarEventos();
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventoDetalleDTO> obtener(@PathVariable Long id) {
        return eventoAppService.obtenerEvento(id)
            .or(() -> eventoAppService.obtenerEvento(id))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
