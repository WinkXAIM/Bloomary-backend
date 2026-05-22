package com.flowary.server.auth;

import com.flowary.server.auth.dto.KakaoTokenResponse;
import com.flowary.server.auth.dto.KakaoUserResponse;
import com.flowary.server.auth.dto.TokenResponse;
import com.flowary.server.user.User;
import com.flowary.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

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

        return new TokenResponse(
                jwtProvider.generateAccessToken(user.getId()),
                jwtProvider.generateRefreshToken(user.getId())
        );
    }
}