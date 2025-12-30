package com.um.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BackendNotifyClientConfig {

    @Bean
    WebClient backendWebClient(
            @Value("${backend.base-url:http://localhost:8080}") String backendBaseUrl
    ) {
        return WebClient.builder()
                .baseUrl(backendBaseUrl)
                .build();
    }
}
