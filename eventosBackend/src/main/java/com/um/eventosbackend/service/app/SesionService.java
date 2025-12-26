package com.um.eventosbackend.service.app;

import com.um.eventosbackend.client.ProxyClient;
import com.um.eventosbackend.domain.AsientoVenta;
import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.domain.VentaLocal;
import com.um.eventosbackend.repository.VentaLocalRepository;
import com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO;
import com.um.eventosbackend.service.dto.app.AsignarNombreRequest;
import com.um.eventosbackend.service.dto.app.SeleccionAsientoRequest;
import com.um.eventosbackend.service.dto.app.SesionCompra;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;

@Service
public class SesionService {
    private final ProxyClient proxyClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final VentaLocalRepository ventaLocalRepository;
    private final com.um.eventosbackend.repository.UserRepository userRepository;
    private final com.um.eventosbackend.repository.EventoLocalRepository eventoLocalRepository;

    @Value("${catedra.api-token}")
    private String tokenCatedra;

    public SesionService(ProxyClient proxyClient,
                         RedisTemplate<String, Object> redisTemplate,
                         VentaLocalRepository ventaLocalRepository,
                         com.um.eventosbackend.repository.UserRepository userRepository,
                         com.um.eventosbackend.repository.EventoLocalRepository eventoLocalRepository) {
        this.proxyClient = proxyClient;
        this.redisTemplate = redisTemplate;
        this.ventaLocalRepository = ventaLocalRepository;
        this.userRepository = userRepository;
        this.eventoLocalRepository = eventoLocalRepository;
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

    public SesionCompra asignarNombreAsiento(HttpSession session, AsignarNombreRequest request) {
        SesionCompra sesion = obtenerSesion(session);
        if (sesion == null) {
            throw new RuntimeException("No existe una sesión activa");
        }

        // Buscamos el asiento específico en la lista de la sesión
        sesion.getAsientos().stream()
            .filter(a -> a.getFila().equals(request.getFila()) && a.getColumna().equals(request.getColumna()))
            .findFirst()
            .ifPresentOrElse(
                a -> {
                    a.setNombre(request.getNombre());
                    a.setApellido(request.getApellido());
                },
                () -> { throw new RuntimeException("Asiento no encontrado en la selección"); }
            );

        // Guardamos los cambios localmente y en Redis
        session.setAttribute(SESSION_KEY, sesion);
        sincronizarConRedis(sesion);
        return sesion;
    }

    @Transactional
    public SesionCompra realizarVentaFinal(HttpSession session) {
        SesionCompra sesion = obtenerSesion(session);
        if (sesion == null || sesion.getAsientos().isEmpty()) {
            throw new RuntimeException("No hay una sesión de compra válida");
        }

        // 1. Calculamos el monto total y preparamos la fecha
        EventoLocal evento = eventoLocalRepository.findByIdCatedra(sesion.getEventoId())
            .orElseThrow(() -> new RuntimeException("No existe el evento con ID Cátedra: " + sesion.getEventoId()));
        java.math.BigDecimal total = evento.getPrecioEntrada().multiply(new java.math.BigDecimal(sesion.getAsientos().size()));

        // 2. Transformamos los asientos al formato "persona" que pide la Cátedra
        java.util.List<Map<String, Object>> asientosParaCatedra = sesion.getAsientos().stream().map(a -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("fila", a.getFila());
            map.put("columna", a.getColumna());
            map.put("persona", a.getNombre() + " " + a.getApellido());
            return map;
        }).toList();

        // 3. Armamos el mapa final para el Proxy
        Map<String, Object> requestAlProxy = new java.util.HashMap<>();
        requestAlProxy.put("eventoId", sesion.getEventoId());
        requestAlProxy.put("fecha", java.time.Instant.now().toString());
        requestAlProxy.put("precioVenta", total);
        requestAlProxy.put("asientos", asientosParaCatedra);

        // 4. Llamada al Proxy
        Map respuesta = proxyClient.realizarVentaConMapa(requestAlProxy, tokenCatedra).block();

        // 5. Persistencia en Postgres (AQUÍ ESTABA EL ERROR)
        if (respuesta != null && (Boolean.TRUE.equals(respuesta.get("exito")) || Boolean.TRUE.equals(respuesta.get("resultado")))) {

            // --- INICIO BLOQUE FALTANTE ---
            VentaLocal venta = new VentaLocal(); // Ahora 'venta' sí existe
            venta.setEstado(VentaLocal.Estado.CONFIRMADA);
            venta.setMontoTotal(total);
            venta.setEvento(evento);
            venta.setUsuario(userRepository.findOneByLogin(sesion.getUsuario()).orElseThrow());

            // Si el proxy devolvió un ID de la cátedra, lo guardamos
            if (respuesta.get("id") != null) {
                venta.setIdCatedra(Long.valueOf(respuesta.get("id").toString()));
            }

            // Convertimos los DTOs de la sesión en entidades AsientoVenta
            for (AsientoSeleccionadoDTO dto : sesion.getAsientos()) {
                AsientoVenta av = new AsientoVenta();
                av.setFila(dto.getFila());
                av.setColumna(dto.getColumna());
                av.setNombre(dto.getNombre());
                av.setApellido(dto.getApellido());
                venta.addAsiento(av); // Esto vincula el asiento a la venta localmente
            }

            // Guardamos en Postgres (CascadeType.ALL se encarga de los asientos)
            ventaLocalRepository.save(venta);
            // --- FIN BLOQUE FALTANTE ---

            // 6. Limpiar sesión y Redis
            limpiarSesion(session);
            if (sesion.getUsuario() != null) {
                redisTemplate.delete(getRedisKey(sesion.getUsuario()));
            }

            sesion.setEtapaActual("FINALIZADO");
            return sesion;
        } else {
            throw new RuntimeException("La Cátedra rechazó la venta final");
        }
    }
}
