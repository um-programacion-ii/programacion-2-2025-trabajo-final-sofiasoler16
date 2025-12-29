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
import java.util.HashMap;
import java.util.List;
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

        // 1. Obtener evento por idCatedra y calcular total
        EventoLocal evento = eventoLocalRepository.findById(sesion.getEventoId())
            .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + sesion.getEventoId()));

        java.math.BigDecimal total = evento.getPrecioEntrada().multiply(new java.math.BigDecimal(sesion.getAsientos().size()));

        // 2. CREAR VENTA EN ESTADO PENDIENTE (REQUISITO ISSUE #24)
        VentaLocal venta = new VentaLocal();
        venta.setEstado(VentaLocal.Estado.PENDIENTE);
        venta.setMontoTotal(total);
        venta.setEvento(evento);
        venta.setUsuario(userRepository.findOneByLogin(sesion.getUsuario()).orElseThrow());

        for (AsientoSeleccionadoDTO dto : sesion.getAsientos()) {
            AsientoVenta av = new AsientoVenta();
            av.setFila(dto.getFila());
            av.setColumna(dto.getColumna());
            av.setNombre(dto.getNombre());
            av.setApellido(dto.getApellido());
            venta.addAsiento(av);
        }

        // Guardamos inicialmente como PENDIENTE
        venta = ventaLocalRepository.save(venta);

        // 3. PREPARAR Y ENVIAR AL PROXY
        try {
            List<Map<String, Object>> asientosCatedra = sesion.getAsientos().stream().map(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("fila", a.getFila());
                map.put("columna", a.getColumna());
                map.put("persona", a.getNombre() + " " + a.getApellido()); // Formato cátedra
                return map;
            }).toList();

            Map<String, Object> requestCatedra = new HashMap<>();
            requestCatedra.put("eventoId", sesion.getEventoId());
            requestCatedra.put("fecha", java.time.Instant.now().toString());
            requestCatedra.put("precioVenta", total);
            requestCatedra.put("asientos", asientosCatedra);

            Map respuesta = proxyClient.realizarVentaConMapa(requestCatedra, tokenCatedra).block();

            // 4. ACTUALIZAR SEGÚN RESPUESTA (REQUISITO ISSUE #24)
            if (respuesta != null && (Boolean.TRUE.equals(respuesta.get("exito")) || Boolean.TRUE.equals(respuesta.get("resultado")))) {
                venta.setEstado(VentaLocal.Estado.CONFIRMADA); // ÉXITO
                if (respuesta.get("ventaId") != null) {
                    venta.setIdCatedra(Long.valueOf(respuesta.get("ventaId").toString()));
                }
                ventaLocalRepository.save(venta);

                limpiarSesion(session);
                redisTemplate.delete(getRedisKey(sesion.getUsuario()));
                sesion.setEtapaActual("FINALIZADO");
                return sesion;
            } else {
                // RECHAZO DE CÁTEDRA -> FALLIDA
                venta.setEstado(VentaLocal.Estado.FALLIDA);
                ventaLocalRepository.save(venta);

                // Retornamos la sesión
                sesion.setEtapaActual("VENTA_RECHAZADA");
                return sesion;
            }
        } catch (Exception e) {
            // ERROR DE RED/SISTEMA -> PENDIENTE
            venta.setEstado(VentaLocal.Estado.PENDIENTE);
            ventaLocalRepository.save(venta);

            sesion.setEtapaActual("ERROR_COMUNICACION");
            return sesion;
        }
    }

    @Transactional
    public void reintentarVenta(VentaLocal venta) {
        try {
            // 1. Preparamos los datos desde la entidad VentaLocal
            List<Map<String, Object>> asientosCatedra = venta.getAsientos().stream().map(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("fila", a.getFila());
                map.put("columna", a.getColumna());
                map.put("persona", a.getNombre() + " " + a.getApellido());
                return map;
            }).toList();

            Map<String, Object> requestCatedra = new HashMap<>();
            requestCatedra.put("eventoId", venta.getEvento().getId());
            requestCatedra.put("fecha", java.time.Instant.now().toString());
            requestCatedra.put("precioVenta", venta.getMontoTotal());
            requestCatedra.put("asientos", asientosCatedra);

            // 2. Llamada al Proxy
            Map respuesta = proxyClient.realizarVentaConMapa(requestCatedra, tokenCatedra).block();

            // 3. Si es exitoso, actualizamos a CONFIRMADA
            if (respuesta != null && (Boolean.TRUE.equals(respuesta.get("exito")) || Boolean.TRUE.equals(respuesta.get("resultado")))) {
                venta.setEstado(VentaLocal.Estado.CONFIRMADA);
                if (respuesta.get("ventaId") != null) {
                    venta.setIdCatedra(Long.valueOf(respuesta.get("ventaId").toString()));
                }
                ventaLocalRepository.save(venta);
            }
        } catch (Exception e) {
            // No hace nada, queda PENDIENTE y vuelve a intentar
        }
    }


    public SesionCompra bloquearAsientosDirecto(HttpSession session, Long eventoId, List<AsientoSeleccionadoDTO> asientos, String username) {
        // 1. Buscamos si ya existe una sesión en Redis para este usuario
        SesionCompra sesion = recuperarSesionPorUsuario(username, session);

        // 2. Si no existe, creamos una nueva
        if (sesion == null) {
            sesion = new SesionCompra();
            sesion.setUsuario(username);
        }

        sesion.setEventoId(eventoId);
        sesion.getAsientos().clear();
        sesion.getAsientos().addAll(asientos);
        sesion.setEtapaActual("DATOS_PERSONALES");

        // 3. Bloqueamos en la cátedra
        Map resultado = proxyClient.bloquearAsientos(eventoId, asientos, tokenCatedra).block();

        boolean esExitoso = (resultado != null) && (
            Boolean.TRUE.equals(resultado.get("creado")) ||
                Boolean.TRUE.equals(resultado.get("resultado"))
        );

        if (esExitoso) {
            sincronizarConRedis(sesion); // Guardamos en Redis para el próximo paso
            session.setAttribute(SESSION_KEY, sesion);
            return sesion;
        } else {
            throw new RuntimeException("Cátedra no pudo bloquear los asientos");
        }
    }

    public SesionCompra realizarVentaFinalDirecta(HttpSession session, List<AsientoSeleccionadoDTO> asientosConNombres, String username) {
        SesionCompra sesion = recuperarSesionPorUsuario(username, session);
        if (sesion == null) throw new RuntimeException("No hay sesión activa");

        // Actualizamos los nombres en la sesión
        sesion.setAsientos(asientosConNombres);
        session.setAttribute(SESSION_KEY, sesion);

        // Llamamos a tu lógica de venta que ya habla con el Proxy y guarda en la DB local
        return realizarVentaFinal(session);
    }
}
