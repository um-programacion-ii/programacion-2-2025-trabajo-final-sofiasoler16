package com.um.proxy.asientos;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proxy")
public class AsientosProxyController {

    private final AsientosRedisService asientosRedisService;

    public AsientosProxyController(AsientosRedisService asientosRedisService) {
        this.asientosRedisService = asientosRedisService;
    }

    @GetMapping("/eventos/{id}/asientos")
    public ResponseEntity<AsientosResponseDTO> getAsientos(
            @PathVariable("id") Long eventoId,
            @RequestParam(name = "filas") Integer filas,
            @RequestParam(name = "columnas") Integer columnas
    ) {
        if (filas == null || columnas == null || filas <= 0 || columnas <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<List<AsientoEstado>> matriz = asientosRedisService.obtenerMatriz(eventoId, filas, columnas);
        return ResponseEntity.ok(new AsientosResponseDTO(eventoId, filas, columnas, matriz));
    }
}
