package com.um.proxy.broker;

import com.um.proxy.service.BackendNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProxyEventoKafkaConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyEventoKafkaConsumer.class);
    private final BackendNotifyService backendNotifyService;

    public ProxyEventoKafkaConsumer(BackendNotifyService backendNotifyService) {
        this.backendNotifyService = backendNotifyService;
    }

    @KafkaListener(
            topics = "${application.kafka.topic}",
            groupId = "sofiasoler16-proxy-final",
            containerFactory = "kafkaListenerContainerFactory",
            errorHandler = "kafkaErrorHandler"
    )
    public void onEventoCambio(String mensaje) {
        LOG.info("📩 SEÑAL RECIBIDA: {}", mensaje);

        try {
            // Limpiamos comillas por si es un JSON string
            String cleanMessage = mensaje.trim().replaceAll("\"", "");

            // Si el mensaje es puramente numérico, notificamos ese ID
            if (cleanMessage.matches("\\d+")) {
                Long idCatedra = Long.valueOf(cleanMessage);
                LOG.info("Notificando cambio para ID específico: {}", idCatedra);
                backendNotifyService.notifyEventoCambio(idCatedra);
            } else {
                // Si es un mensaje de texto (como el que recibiste), sincronizamos todo
                LOG.info("Mensaje de texto detectado ('{}'). Iniciando sincronización general.", cleanMessage);
                backendNotifyService.notifyEventoCambio(0L); // 0L indica "todo"
            }
        } catch (Exception e) {
            LOG.error("❌ Error procesando el cuerpo del mensaje: {}", mensaje, e);
        }
    }
}