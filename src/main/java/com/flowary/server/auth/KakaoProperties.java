package com.flowary.server.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(String restApiKey, String redirectUri, String clientRedirectUri, String clientSecret) {
}