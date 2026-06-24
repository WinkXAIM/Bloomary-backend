package com.flowary.server.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            KakaoProfile profile
    ) {
    }

    public record KakaoProfile(
            String nickname
    ) {
    }
}