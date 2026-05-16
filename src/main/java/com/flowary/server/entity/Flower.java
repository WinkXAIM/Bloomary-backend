package com.flowary.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flower_id")
    private Integer flowerId; // INT Auto Increment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Analysis_id")
    private Analysis analysis;

    @Column(name = "name_ko", length = 100)
    private String nameKo; // VARCHAR(100)

    @Column(name = "name_en", length = 100)
    private String nameEn; // VARCHAR(100)

    @Column(columnDefinition = "TEXT")
    private String meaning; // TEXT

    @Column(length = 100)
    private String box2d; // VARCHAR(100)
}