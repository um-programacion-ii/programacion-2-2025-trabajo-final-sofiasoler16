package com.um.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ProxyProperties {
    private final Backend backend = new Backend();
    private final Kafka kafka = new Kafka();

    public Backend getBackend() { return backend; }
    public Kafka getKafka() { return kafka; }

    public static class Backend {
        private String baseUrl;
        private String notifyPath;
        // Getters and Setters
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getNotifyPath() { return notifyPath; }
        public void setNotifyPath(String notifyPath) { this.notifyPath = notifyPath; }
    }

    public static class Kafka {
        private String topic;
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }
}