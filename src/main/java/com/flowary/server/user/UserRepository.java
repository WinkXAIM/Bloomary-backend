package com.flowary.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(Long kakaoId);

    default User upsert(Long kakaoId, String nickname) {
        return findByKakaoId(kakaoId)
                .map(user -> {
                    user.updateNickname(nickname);
                    return save(user);
                })
                .orElseGet(() -> save(
                        User.builder()
                                .kakaoId(kakaoId)
                                .nickname(nickname)
                                .build()
                ));
    }
}