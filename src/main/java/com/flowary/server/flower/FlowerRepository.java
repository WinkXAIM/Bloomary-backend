package com.flowary.server.flower;

import com.flowary.server.analysis.AnalysisFlower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FlowerRepository extends JpaRepository<AnalysisFlower, Long> {
}