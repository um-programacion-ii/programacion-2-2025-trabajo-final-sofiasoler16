package com.um.eventosbackend.broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.um.eventosbackend.service.notify.BackendNotifyService;

//@Component
//public class EventoKafkaConsumer {
//
//    private static final Logger log = LoggerFactory.getLogger(EventoKafkaConsumer.class);
//
//    private final BackendNotifyService backendNotifyService;
//
//    public EventoKafkaConsumer(BackendNotifyService backendNotifyService) {
//        this.backendNotifyService = backendNotifyService;
//    }
//
//    @KafkaListener(
//        topics = "eventos.cambios",
//        groupId = "sofiasoler16"
////      id de github
//    )
//    public void onEventoCambio(String mensaje) {
//        log.info("📩 Cambio de evento recibido desde Kafka: {}", mensaje);
//
////        Long idCatedra = Long.valueOf(mensaje.trim());
////        backendNotifyService.notifyEventoCambio(idCatedra);
//    }
//}
