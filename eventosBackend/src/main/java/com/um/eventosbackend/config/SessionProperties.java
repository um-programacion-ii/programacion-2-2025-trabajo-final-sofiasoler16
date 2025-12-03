package com.um.eventosbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "session")
public class SessionProperties {

    private int timeoutMinutes;

    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }
}
