package com.devpath.backend.repository;

import com.devpath.backend.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
}