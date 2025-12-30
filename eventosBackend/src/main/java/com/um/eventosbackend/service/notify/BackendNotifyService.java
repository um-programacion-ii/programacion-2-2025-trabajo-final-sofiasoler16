//package com.um.eventosbackend.service.notify;
//
//import com.um.eventosbackend.service.dto.proxy.EventoNotifyDTO;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Service
//public class BackendNotifyService {
//
//    private final WebClient notifyWebClient;
//
//    public BackendNotifyService(WebClient notifyWebClient) {
//        this.notifyWebClient = notifyWebClient;
//    }
//
////    public void notifyEventoCambio(Long idCatedra) {
////        EventoNotifyDTO dto = new EventoNotifyDTO();
////        dto.setIdCatedra(idCatedra);
////
////        notifyWebClient
////            .post()
////            .uri("/api/admin/eventos/notify")
////            .contentType(MediaType.APPLICATION_JSON)
////            .bodyValue(dto)
////            .retrieve()
////            .toBodilessEntity()
////            .block(); // OK para este TP; después se puede hacer async
////    }
//}
