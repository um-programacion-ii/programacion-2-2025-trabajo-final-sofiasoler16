package com.um.eventosbackend.service.app;

import com.um.eventosbackend.service.dto.app.SesionCompra;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SesionService {
    private static final String SESSION_KEY = "SESION_COMPRA";

    public void guardarSesion(HttpSession session, SesionCompra datos) {
        session.setAttribute(SESSION_KEY, datos);
    }

    public SesionCompra obtenerSesion(HttpSession session) {
        return (SesionCompra) session.getAttribute(SESSION_KEY);
    }
}
