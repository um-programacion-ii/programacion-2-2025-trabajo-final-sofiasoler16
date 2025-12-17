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

    @KafkaListener(topics = "eventos.cambios", groupId = "sofiasoler16")
    public void onEventoCambio(String mensaje) {
        log.info("📩 Proxy recibió cambio desde Kafka: {}", mensaje);

        Long idCatedra = Long.valueOf(mensaje.trim());
        backendNotifyService.notifyEventoCambio(idCatedra);
    }
}
