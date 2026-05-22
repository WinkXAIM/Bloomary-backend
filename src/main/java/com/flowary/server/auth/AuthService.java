package com.flowary.server.auth;

import com.flowary.server.auth.dto.KakaoTokenResponse;
import com.flowary.server.auth.dto.KakaoUserResponse;
import com.flowary.server.auth.dto.TokenResponse;
import com.flowary.server.user.User;
import com.flowary.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Long validateAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header missing or malformed");
        }
        String token = authorizationHeader.substring(7);
        if (!jwtProvider.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired access token");
        }
        return jwtProvider.getUserId(token);
    }

    public TokenResponse kakaoCallback(String code) {
        KakaoTokenResponse kakaoToken = kakaoClient.getToken(code);
        KakaoUserResponse kakaoUser = kakaoClient.getUserInfo(kakaoToken.accessToken());

        String nickname = kakaoUser.kakaoAccount() != null
                && kakaoUser.kakaoAccount().profile() != null
                ? kakaoUser.kakaoAccount().profile().nickname()
                : null;

        User user = userRepository.findByKakaoId(kakaoUser.id())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .kakaoId(kakaoUser.id())
                                .nickname(nickname)
                                .build()
                ));

        System.out.println("nickname:" + nickname + " id:" + kakaoUser.id());

        return new TokenResponse(
                jwtProvider.generateAccessToken(user.getId()),
                jwtProvider.generateRefreshToken(user.getId())
        );
    }
}