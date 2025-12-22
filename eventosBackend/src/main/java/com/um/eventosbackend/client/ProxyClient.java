package com.um.eventosbackend.client;

import com.um.eventosbackend.service.dto.app.AsientoSeleccionadoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
public class ProxyClient {

    private final WebClient webClient;

    public ProxyClient(WebClient.Builder webClientBuilder,
                       @Value("${proxy.base-url:http://localhost:8081}") String proxyUrl) {
        String finalUrl = proxyUrl.contains("8080") ? "http://localhost:8081" : proxyUrl;
        this.webClient = webClientBuilder.baseUrl(finalUrl).build();
    }

    public Mono<Map> bloquearAsientos(Long eventoId, List<AsientoSeleccionadoDTO> asientos, String token) {
        Map<String, Object> request = Map.of(
            "eventoId", eventoId,
            "asientos", asientos
        );

        return this.webClient.post()
            .uri("/proxy/bloquear")
            .header("Authorization", "Bearer " + token)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map.class);
    }
}
