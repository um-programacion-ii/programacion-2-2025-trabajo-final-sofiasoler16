package com.um.eventosbackend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfigLogger {

    private static final Logger log = LoggerFactory.getLogger(ConfigLogger.class);

    private final CatedraProperties catedraProperties;
    private final ProxyProperties proxyProperties;
    private final SessionProperties sessionProperties;

    public ConfigLogger(
        CatedraProperties catedraProperties,
        ProxyProperties proxyProperties,
        SessionProperties sessionProperties
    ) {
        this.catedraProperties = catedraProperties;
        this.proxyProperties = proxyProperties;
        this.sessionProperties = sessionProperties;
    }

    @PostConstruct
    public void logConfig() {
        log.info("Catedra base-url = {}", catedraProperties.getBaseUrl());
        log.info("Catedra api-token (primeros 5 chars) = {}",
            catedraProperties.getApiToken() != null
                ? catedraProperties.getApiToken().substring(0, Math.min(5, catedraProperties.getApiToken().length())) + "..."
                : "null");
        log.info("Proxy base-url = {}", proxyProperties.getBaseUrl());
        log.info("Session timeout-minutes = {}", sessionProperties.getTimeoutMinutes());
    }
}
