package com.um.proxy.broker;

import com.um.proxy.service.BackendNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProxyEventoKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProxyEventoKafkaConsumer.class);
    private final BackendNotifyService backendNotifyService;

    public ProxyEventoKafkaConsumer(BackendNotifyService backendNotifyService) {
        this.backendNotifyService = backendNotifyService;
    }

    // Escuchamos los dos posibles nombres y cambiamos el grupo a V3
    @KafkaListener(topics = {"eventos", "evento-actualizado", "eventos.cambios"}, groupId = "sofiasoler16-proxy-v3")
    public void onEventoCambio(String mensaje) {
        // Log para ver el mensaje CRUDO que llega
        log.info("📩 NOTIFICACIÓN RECIBIDA DESDE KAFKA: '{}'", mensaje);

        try {
            // Limpiamos el mensaje por si viene con comillas de JSON
            String cleanId = mensaje.trim().replaceAll("\"", "");
            Long idCatedra = Long.valueOf(cleanId);

            log.info("Gatillando sincronización en el backend para ID: {}", idCatedra);
            backendNotifyService.notifyEventoCambio(idCatedra);
        } catch (Exception e) {
            log.error("Error al procesar el ID de la cátedra: {}", mensaje);
        }
    }
}
