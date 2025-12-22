package com.um.proxy.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final WebClient webClient;

    public ProxyController(WebClient.Builder webClientBuilder,
                           @Value("${app.catedra.base-url}") String catedraUrl) {
        this.webClient = webClientBuilder.baseUrl(catedraUrl).build();
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/bloquear")
    public Mono<ResponseEntity<Map>> bloquearAsientos(@RequestBody Map<String, Object> request,
                                                      @RequestHeader("Authorization") String authHeader) {
        return this.webClient.post()
                // AGREGAMOS EL /endpoints/ QUE FALTABA
                .uri("/api/endpoints/v1/bloquear-asientos")
                .header("Authorization", authHeader)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Cátedra dice: " + errorBody)));
                })
                .toEntity(Map.class);
    }
}
