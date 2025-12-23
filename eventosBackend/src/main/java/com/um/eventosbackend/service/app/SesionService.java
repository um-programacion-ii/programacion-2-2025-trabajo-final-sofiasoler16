package com.um.eventosbackend.service.app;

import com.um.eventosbackend.client.ProxyClient;
import com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO;
import com.um.eventosbackend.service.dto.app.SeleccionAsientoRequest;
import com.um.eventosbackend.service.dto.app.SesionCompra;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class SesionService {
    private final ProxyClient proxyClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${catedra.api-token}")
    private String tokenCatedra;

    public SesionService(ProxyClient proxyClient, RedisTemplate<String, Object> redisTemplate) {
        this.proxyClient = proxyClient;
        this.redisTemplate = redisTemplate;
    }

    private String getRedisKey(String username) {
        return "compra_activa:" + username;
    }

    public static final String SESSION_KEY = "SESION_COMPRA_ACTIVA";

    // Método para guardar en Redis cada vez que algo cambie
    private void sincronizarConRedis(SesionCompra sesion) {
        if (sesion != null && sesion.getUsuario() != null) {
            redisTemplate.opsForValue().set(getRedisKey(sesion.getUsuario()), sesion, Duration.ofMinutes(30));
        }
    }

    // Crear o renovar
    public SesionCompra renovarSesion(HttpSession session, SesionCompra nuevosDatos) {
        session.setAttribute(SESSION_KEY, nuevosDatos);
        sincronizarConRedis(nuevosDatos);
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
            sesion.getAsientos().add(new AsientoSeleccionadoDTO(request.getFila(), request.getColumna()));
        }

        session.setAttribute(SESSION_KEY, sesion);
        sincronizarConRedis(sesion);
        return sesion;
    }

    public SesionCompra bloquearAsientosEnSesion(HttpSession session) {
        SesionCompra sesion = obtenerSesion(session);
        if (sesion == null || sesion.getAsientos().isEmpty()) {
            throw new RuntimeException("No hay asientos seleccionados para bloquear");
        }

        Map resultado = proxyClient.bloquearAsientos(sesion.getEventoId(), sesion.getAsientos(), tokenCatedra).block();

        boolean esExitoso = (resultado != null) && (
            Boolean.TRUE.equals(resultado.get("creado")) ||
                Boolean.TRUE.equals(resultado.get("resultado"))
        );

        if (esExitoso) {
            sesion.setEtapaActual("DATOS_PERSONALES");
            session.setAttribute(SESSION_KEY, sesion);
            return sesion;
        } else {
            Object mensajeObj = (resultado != null) ? resultado.get("resultado") : "Error desconocido";
            throw new RuntimeException("Cátedra no pudo bloquear: " + String.valueOf(mensajeObj));
        }
    }

    public SesionCompra recuperarSesionPorUsuario(String username, HttpSession session) {
        // Buscamos en Redis si este usuario ya tenía algo empezado
        SesionCompra sesionGlobal = (SesionCompra) redisTemplate.opsForValue().get(getRedisKey(username));

        if (sesionGlobal != null) {
            session.setAttribute(SESSION_KEY, sesionGlobal); // La bajamos a la sesión local
        }
        return sesionGlobal;
    }
}
