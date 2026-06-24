package com.flowary.server.flower;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "upload")
public record UploadProperties(String tempDir, String flowersDir, long expiryHours) {
}