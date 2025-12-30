package com.um.proxy.service;


import com.um.proxy.service.dto.EventoNotifyDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BackendNotifyService {

    private final WebClient backendWebClient;

    public BackendNotifyService(WebClient backendWebClient) {
        this.backendWebClient = backendWebClient;
    }

    public void notifyEventoCambio(Long idCatedra) {
        EventoNotifyDTO dto = new EventoNotifyDTO();
        dto.setIdCatedra(idCatedra);

        backendWebClient.post()
                .uri("/api/public/eventos/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
