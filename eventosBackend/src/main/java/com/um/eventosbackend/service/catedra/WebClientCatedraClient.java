package com.um.eventosbackend.service.catedra;

import com.um.eventosbackend.service.dto.catedra.CatedraEventoDetalleDTO;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoResumenDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class WebClientCatedraClient implements CatedraClient {

    private static final Logger log = LoggerFactory.getLogger(WebClientCatedraClient.class);

    // Paths relativos al base-url de la cátedra
    private static final String EVENTOS_RESUMIDOS_PATH = "/api/endpoints/v1/eventos-resumidos";
    private static final String EVENTOS_PATH = "/api/endpoints/v1/eventos";
    private static final String EVENTO_DETALLE_PATH = "/api/endpoints/v1/evento/{id}";

    private final WebClient webClient;

    public WebClientCatedraClient(@Qualifier("catedraWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CatedraEventoResumenDTO> listarEventosResumidos() {
        log.debug("Llamando a cátedra: {}", EVENTOS_RESUMIDOS_PATH);

        return webClient
            .get()
            .uri(EVENTOS_RESUMIDOS_PATH)
            .retrieve()
            .bodyToFlux(CatedraEventoResumenDTO.class)
            .collectList()
            .onErrorResume(error -> {
                log.error("Error consultando eventos resumidos en cátedra", error);
                return Mono.just(List.of());
            })
            .block();
    }

    @Override
    public List<CatedraEventoDetalleDTO> listarEventos() {
        log.debug("Llamando a cátedra: {}", EVENTOS_PATH);

        return webClient
            .get()
            .uri(EVENTOS_PATH)
            .retrieve()
            .bodyToFlux(CatedraEventoDetalleDTO.class)
            .collectList()
            .onErrorResume(error -> {
                log.error("Error consultando eventos en cátedra", error);
                return Mono.just(List.of());
            })
            .block();
    }

    @Override
    public CatedraEventoDetalleDTO obtenerEventoPorId(Long id) {
        log.debug("Llamando a cátedra: {} con id={}", EVENTO_DETALLE_PATH, id);

        return webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(EVENTO_DETALLE_PATH).build(id))
            .retrieve()
            .bodyToMono(CatedraEventoDetalleDTO.class)
            .block();
    }
}
