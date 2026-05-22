package com.flowary.server.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.server")
public record AiProperties(String url) {
}