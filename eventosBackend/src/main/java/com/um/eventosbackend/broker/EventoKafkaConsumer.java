package com.um.eventosbackend.broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventoKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventoKafkaConsumer.class);

    @KafkaListener(
        topics = "eventos.cambios",
        groupId = "sofiasoler16"
    )
    public void onEventoCambio(String mensaje) {
        log.info("📩 Cambio de evento recibido desde Kafka: {}", mensaje);

        // En el futuro:
        // - parsear JSON
        // - llamar a EventoSyncService
    }
}
