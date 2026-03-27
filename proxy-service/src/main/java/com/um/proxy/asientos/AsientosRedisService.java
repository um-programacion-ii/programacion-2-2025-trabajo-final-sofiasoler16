package com.um.proxy.asientos;

import com.fasterxml.jackson.databind.ObjectMapper; // Necesario para procesar JSON
import java.util.*;

import com.um.proxy.service.dto.CatedraAsientosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AsientosRedisService {

    private static final Logger log = LoggerFactory.getLogger(AsientosRedisService.class);
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper; // Para leer el JSON de la cátedra

    @Value("${spring.data.redis.asientos-key-pattern:evento_%s}")
    private String keyPattern;

    public AsientosRedisService(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public List<List<AsientoEstado>> obtenerMatriz(Long eventoId, int filas, int columnas) {
        List<List<AsientoEstado>> matriz = nuevaMatrizLibre(filas, columnas);
        String key = String.format(keyPattern, eventoId);
        String json = redis.opsForValue().get(key); // 1. Obtener el String JSON de Redis


        if (json == null || json.isEmpty()) return matriz;

        try {
            CatedraAsientosDTO datosCatedra = objectMapper.readValue(json, CatedraAsientosDTO.class);

            if (datosCatedra.getAsientos() != null) {
                for (CatedraAsientosDTO.AsientoDTO a : datosCatedra.getAsientos()) {
                    int f = a.getFila() - 1;
                    int c = a.getColumna() - 1;
                    if (f >= 0 && f < filas && c >= 0 && c < columnas) {
                        matriz.get(f).set(c, AsientoEstado.fromRedisValue(a.getEstado()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error procesando JSON de cátedra", e);
        }
        return matriz;
    }

    private static List<List<AsientoEstado>> nuevaMatrizLibre(int filas, int columnas) {
        List<List<AsientoEstado>> matriz = new ArrayList<>(filas);
        for (int i = 0; i < filas; i++) {
            List<AsientoEstado> fila = new ArrayList<>(columnas);
            for (int j = 0; j < columnas; j++) {
                fila.add(AsientoEstado.LIBRE); // Por defecto todos libres
            }
            matriz.add(fila);
        }
        return matriz;
    }
}