package com.flowary.server.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long accessTokenExpiry, long refreshTokenExpiry) {
}