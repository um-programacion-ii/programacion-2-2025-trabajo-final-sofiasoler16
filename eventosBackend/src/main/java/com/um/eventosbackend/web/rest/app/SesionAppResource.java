package com.um.eventosbackend.web.rest.app;

import com.um.eventosbackend.service.app.SesionService;
import com.um.eventosbackend.service.dto.app.AsignarNombreRequest;
import com.um.eventosbackend.service.dto.app.SeleccionAsientoRequest;
import com.um.eventosbackend.service.dto.app.SesionCompra;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/app")
public class SesionAppResource {

    private final SesionService sesionService;

    public SesionAppResource(SesionService sessionService) {
        this.sesionService = sessionService;
    }

    @PostMapping("/sesion")
    public ResponseEntity<SesionCompra> crearORenovarSesion(@RequestBody SesionCompra datos, HttpSession session, Principal principal) {
        datos.setUsuario(principal.getName());
        SesionCompra sesion = sesionService.renovarSesion(session, datos);
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/sesion")
    public ResponseEntity<SesionCompra> consultarSesion(HttpSession session) {
        SesionCompra sesion = sesionService.obtenerSesion(session);
        if (sesion == null) {
            return ResponseEntity.noContent().build(); // 204 si no hay sesión
        }
        return ResponseEntity.ok(sesion);
    }

    @DeleteMapping("/sesion")
    public ResponseEntity<Void> cerrarSesion(HttpSession session) {
        sesionService.limpiarSesion(session);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seleccion")
    public ResponseEntity<SesionCompra> seleccionarAsiento(@RequestBody SeleccionAsientoRequest request, HttpSession session) {
        try {
            SesionCompra sesion = sesionService.seleccionarAsiento(session, request);
            return ResponseEntity.ok(sesion);
        } catch (RuntimeException e) {
            // Devolvemos un 400 Bad Request si supera los 4 asientos
            return ResponseEntity.badRequest().header("X-error-message", e.getMessage()).build();
        }
    }

    @PostMapping("/bloquear")
    public ResponseEntity<SesionCompra> bloquearAsientos(HttpSession session) {
        try {
            SesionCompra sesion = sesionService.bloquearAsientosEnSesion(session);
            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("X-error-message", e.getMessage()).build();
        }
    }

    @GetMapping("/sesion/recuperar")
    public ResponseEntity<SesionCompra> recuperar(HttpSession session, Principal principal) {
        // Principal trae automáticamente el nombre del usuario logueado por el token
        String username = principal.getName();
        SesionCompra sesion = sesionService.recuperarSesionPorUsuario(username, session);

        if (sesion == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/nombres")
    public ResponseEntity<SesionCompra> asignarNombres(@RequestBody AsignarNombreRequest request, HttpSession session) {
        try {
            SesionCompra sesion = sesionService.asignarNombreAsiento(session, request);
            return ResponseEntity.ok(sesion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().header("X-error-message", e.getMessage()).build();
        }
    }
}
