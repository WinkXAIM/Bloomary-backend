package com.flowary.server.analysis;

import com.flowary.server.analysis.Analysis;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flowers")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisFlower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flower_id")
    private Long flowerId; // ERD: flower_id INT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    private Analysis analysis; // ERD: Analysis_id VARCHAR(50)

    @Column(name = "name_ko", nullable = false, length = 100)
    private String nameKo; // ERD: name_ko VARCHAR(100)

    @Column(name = "name_en", length = 100)
    private String nameEn; // ERD: name_en VARCHAR(100)

    @Column(columnDefinition = "TEXT")
    private String meaning; // ERD: meaning TEXT

    @Column(length = 100)
    private String box2d; // ERD: box2d VARCHAR(100)
}