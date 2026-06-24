package com.flowary.server.auth.dto;

public record TokenResponse(String accessToken, String refreshToken) {
}