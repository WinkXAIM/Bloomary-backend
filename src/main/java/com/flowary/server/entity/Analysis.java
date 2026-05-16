package com.flowary.server.entity; // 내 프로젝트 패키지 경로

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Analysis {

    @Id
    @Column(length = 50)
    private String id; // VARCHAR(50)

    @Column(name = "user_id", length = 50)
    private String userId; // VARCHAR(50)

    @Column(name = "image_url", length = 500)
    private String imageUrl; // VARCHAR(500)

    @Column(length = 255)
    private String summary; // VARCHAR(255)

    @Column(columnDefinition = "TEXT")
    private String content; // TEXT

    @Column(columnDefinition = "TEXT")
    private String story; // TEXT

    @Column(name = "created_at")
    private LocalDateTime createdAt; // DATETIME

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL)
    private List<Flower> flowers;
}