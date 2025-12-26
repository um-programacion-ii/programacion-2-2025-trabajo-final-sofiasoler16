package com.um.eventosbackend.service.app;

import com.um.eventosbackend.domain.VentaLocal;
import com.um.eventosbackend.repository.VentaLocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VentaRetryService {
    private final Logger log = LoggerFactory.getLogger(VentaRetryService.class);
    private final VentaLocalRepository ventaLocalRepository;
    private final SesionService sesionService;

    public VentaRetryService(VentaLocalRepository ventaLocalRepository, SesionService sesionService) {
        this.ventaLocalRepository = ventaLocalRepository;
        this.sesionService = sesionService;
    }

    // Se ejecuta cada 1 minuto (60000 ms)
    @Scheduled(fixedDelay = 60000)
    public void reintentarVentasPendientes() {
        List<VentaLocal> pendientes = ventaLocalRepository.findAllByEstado(VentaLocal.Estado.PENDIENTE);

        if (pendientes.isEmpty()) return;

        log.info("Encontradas {} ventas pendientes. Iniciando reintentos...", pendientes.size());

        for (VentaLocal venta : pendientes) {
            try {
                log.info("Reintentando venta ID: {}", venta.getId());
                sesionService.reintentarVenta(venta);
            } catch (Exception e) {
                log.error("Fallo el reintento para la venta {}: {}", venta.getId(), e.getMessage());
            }
        }
    }
}
