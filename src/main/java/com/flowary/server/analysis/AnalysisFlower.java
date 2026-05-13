package com.flowary.server.analysis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "analysis_flowers")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisFlower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameKo;

    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String meaning;
}