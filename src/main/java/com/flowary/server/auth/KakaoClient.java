package com.flowary.server.auth;

import com.flowary.server.auth.dto.KakaoTokenResponse;
import com.flowary.server.auth.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoClient {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestClient restClient = RestClient.create();
    private final KakaoProperties kakaoProperties;

    public KakaoTokenResponse getToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.restApiKey());
        params.add("redirect_uri", kakaoProperties.redirectUri());
        params.add("code", code);
        params.add("client_secret", kakaoProperties.clientSecret());

        return restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserResponse getUserInfo(String kakaoAccessToken) {
        return restClient.get()
                .uri(USER_INFO_URL)
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(KakaoUserResponse.class);
    }
}