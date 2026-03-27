//package com.um.proxy.service;
//
//import com.um.proxy.service.dto.EventoNotifyDTO;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Service
//public class BackendNotifyClient {
//
//    private static final Logger log = LoggerFactory.getLogger(BackendNotifyClient.class);
//
//    private final WebClient webClient;
//
//    @Value("${backend.base-url}")
//    private String backendBaseUrl;
//
//    @Value("${backend.notify-token}")
//    private String notifyToken;
//
//    public BackendNotifyClient(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    public void notifyCambioEvento(Long idCatedra) {
//        log.info("Notificando backend por cambio de evento. idCatedra={}", idCatedra);
//
//        webClient.post()
//                .uri(backendBaseUrl + "/api/admin/eventos/notify")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + notifyToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(new EventoNotifyDTO(idCatedra))
//                .retrieve()
//                .toBodilessEntity()
//                .doOnSuccess(r -> log.info("Backend notificado correctamente"))
//                .doOnError(e -> log.error("Error notificando backend", e))
//                .subscribe();
//    }
//}
