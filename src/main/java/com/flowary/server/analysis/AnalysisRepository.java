package com.flowary.server.analysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findByUserId(Long userId);

    @Query("SELECT a FROM Analysis a JOIN FETCH a.flowers WHERE a.id = :id")
    Optional<Analysis> findByIdWithFlowers(@Param("id") Long id);
}