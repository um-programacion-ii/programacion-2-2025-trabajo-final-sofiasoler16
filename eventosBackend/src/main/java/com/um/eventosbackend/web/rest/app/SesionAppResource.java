package com.um.eventosbackend.web.rest.app;

import com.um.eventosbackend.service.app.SesionService;
import com.um.eventosbackend.service.dto.app.SesionCompra;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app")
public class SesionAppResource {

    private final SesionService sessionService;

    public SesionAppResource(SesionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/sesion")
    public ResponseEntity<SesionCompra> crearORenovarSesion(@RequestBody SesionCompra datos, HttpSession session) {
        SesionCompra sesion = sessionService.renovarSesion(session, datos);
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/sesion")
    public ResponseEntity<SesionCompra> consultarSesion(HttpSession session) {
        SesionCompra sesion = sessionService.obtenerSesion(session);
        if (sesion == null) {
            return ResponseEntity.noContent().build(); // 204 si no hay sesión
        }
        return ResponseEntity.ok(sesion);
    }

    @DeleteMapping("/sesion")
    public ResponseEntity<Void> cerrarSesion(HttpSession session) {
        sessionService.limpiarSesion(session);
        return ResponseEntity.noContent().build();
    }
}
