package com.um.eventosbackend.service.app;

import com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO;
import com.um.eventosbackend.service.dto.app.SeleccionAsientoRequest;
import com.um.eventosbackend.service.dto.app.SesionCompra;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SesionService {
    public static final String SESSION_KEY = "SESION_COMPRA_ACTIVA";

    // Crear o renovar
    public SesionCompra renovarSesion(HttpSession session, SesionCompra nuevosDatos) {
        session.setAttribute(SESSION_KEY, nuevosDatos);
        return nuevosDatos;
    }

    // Obtener la sesión actual
    public SesionCompra obtenerSesion(HttpSession session) {
        return (SesionCompra) session.getAttribute(SESSION_KEY);
    }

    // Limpiar la sesión
    public void limpiarSesion(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public SesionCompra seleccionarAsiento(HttpSession session, SeleccionAsientoRequest request) {
        SesionCompra sesion = obtenerSesion(session);
        if (sesion == null) return null;

        // Toggle: quitar si ya existe
        boolean removido = sesion.getAsientos().removeIf(a ->
            a.getFila().equals(request.getFila()) &&
                a.getColumna().equals(request.getColumna())
        );

        // Validar máximo de 4 asientos
        if (!removido) {
            if (sesion.getAsientos().size() >= 4) {
                throw new RuntimeException("No puedes seleccionar más de 4 asientos");
            }
            // Corregido: pasar fila y columna (no fila dos veces)
            sesion.getAsientos().add(new AsientoSeleccionadoDTO(request.getFila(), request.getColumna()));
        }

        session.setAttribute(SESSION_KEY, sesion);
        return sesion;
    }
}
