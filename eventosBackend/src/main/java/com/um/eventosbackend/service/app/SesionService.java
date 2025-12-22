package com.um.eventosbackend.service.app;

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

    // errar/Limpiar la sesión
    public void limpiarSesion(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }
}
