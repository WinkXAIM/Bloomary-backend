package com.flowary.server.flower;

import com.flowary.server.analysis.AnalysisFlower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlowerRepository extends JpaRepository<AnalysisFlower, Long> {
    List<AnalysisFlower> findByAnalysisId(Long analysisId);
}