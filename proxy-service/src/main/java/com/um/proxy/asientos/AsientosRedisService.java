package com.um.proxy.asientos;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AsientosRedisService {

    private final StringRedisTemplate redis;

    @Value("${app.redis.asientos-key-pattern:evento:%s:asientos}")
    private String keyPattern;

    public AsientosRedisService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public List<List<AsientoEstado>> obtenerMatriz(Long eventoId, int filas, int columnas) {
        List<List<AsientoEstado>> matriz = nuevaMatrizLibre(filas, columnas);

        String key = String.format(keyPattern, eventoId);


        Map<Object, Object> all;
        try {
            all = redis.opsForHash().entries(key);
        } catch (Exception e) {
            return matriz;
        }

        if (all == null || all.isEmpty()) {
            return matriz;
        }

        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            String field = Objects.toString(entry.getKey(), null);
            String value = Objects.toString(entry.getValue(), null);
            if (field == null) continue;

            String[] parts = field.split(":");
            if (parts.length != 2) continue;

            try {
                int f1 = Integer.parseInt(parts[0]); // Redis usa 1-based
                int c1 = Integer.parseInt(parts[1]); // Redis usa 1-based

                int f = f1 - 1; // pasamos a 0-based
                int c = c1 - 1;

                if (f >= 0 && f < filas && c >= 0 && c < columnas) {
                    matriz.get(f).set(c, AsientoEstado.fromRedisValue(value));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return matriz;
    }

    private static List<List<AsientoEstado>> nuevaMatrizLibre(int filas, int columnas) {
        List<List<AsientoEstado>> matriz = new ArrayList<>(filas);
        for (int i = 0; i < filas; i++) {
            List<AsientoEstado> fila = new ArrayList<>(columnas);
            for (int j = 0; j < columnas; j++) {
                fila.add(AsientoEstado.LIBRE);
            }
            matriz.add(fila);
        }
        return matriz;
    }
}
