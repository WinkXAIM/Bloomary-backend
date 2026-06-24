package com.flowary.server.auth;

import com.flowary.server.auth.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";

    private final KakaoProperties kakaoProperties;
    private final AuthService authService;

    @GetMapping("/kakao")
    public ResponseEntity<Void> kakaoLogin() {
        URI redirectUrl = UriComponentsBuilder.fromUri(URI.create(KAKAO_AUTH_URL))
                .queryParam("client_id", kakaoProperties.restApiKey())
                .queryParam("redirect_uri", kakaoProperties.redirectUri())
                .queryParam("response_type", "code")
                .build()
                .toUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUrl)
                .build();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> kakaoCallback(@RequestParam String code) {
        System.out.println("code: " + code);
        TokenResponse tokens = authService.kakaoCallback(code);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.accessToken())
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .location(URI.create(kakaoProperties.clientRedirectUri()))
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Long>> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE);
        return ResponseEntity.ok(Map.of("userId", userId));
    }
}