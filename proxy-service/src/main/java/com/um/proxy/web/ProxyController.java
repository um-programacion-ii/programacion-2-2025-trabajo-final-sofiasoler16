package com.um.proxy.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
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

    @PostMapping("/realizar-venta")
    public Mono<ResponseEntity<Map>> realizarVenta(@RequestBody Map<String, Object> request,
                                                   @RequestHeader("Authorization") String authHeader) {
        return this.webClient.post()
                .uri("/api/endpoints/v1/realizar-venta")
                .header("Authorization", authHeader)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Cátedra rechazó venta: " + errorBody)));
                })
                .toEntity(Map.class)
                .map(response -> {
                    Map<String, Object> body = response.getBody();
                    if (body != null && response.getStatusCode().is2xxSuccessful()) {
                        body.put("exito", true);
                    }
                    return ResponseEntity.status(response.getStatusCode()).body(body);
                });
    }
    @GetMapping("/ventas")
    public Mono<ResponseEntity<List<Map>>> listarVentas(@RequestHeader("Authorization") String authHeader) {
        return this.webClient.get()
                .uri("/api/endpoints/v1/listar-ventas") // La URL real de la cátedra
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Cátedra dice: " + errorBody)));
                })
                .toEntityList(Map.class);
    }
}
