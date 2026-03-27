package com.um.eventosbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

@Configuration
public class CatedraClientConfiguration {

    private final CatedraProperties catedraProperties;

    public CatedraClientConfiguration(CatedraProperties catedraProperties) {
        this.catedraProperties = catedraProperties;
    }

    @Bean
    public WebClient catedraWebClient() {
        return WebClient.builder()
            .baseUrl(catedraProperties.getBaseUrl())
            .defaultHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + catedraProperties.getApiToken()
            )
            .build();
    }
}
