package com.devpath.backend.service;

import com.devpath.backend.dto.*;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.repository.ResumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CareerReportService {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;
    private final LearningRoadmapService roadmapService;
    private final JobRecommendationService jobRecommendationService;

    public CareerReportResponse generateReport(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        try {

            Map<String, Object> analysis =
                    objectMapper.readValue(
                            resume.getAnalysisResult().getAnalysisJson(),
                            new TypeReference<Map<String, Object>>() {}
                    );

            List<Map<String, Object>> roles =
                    (List<Map<String, Object>>) analysis.get("best_fit_roles");

            List<RoleScore> roleScores = new ArrayList<>();

            for (Map<String, Object> role : roles) {

                roleScores.add(
                        RoleScore.builder()
                                .role(role.get("role").toString())
                                .score((int) role.get("score"))
                                .build()
                );
            }

            Map<String, Object> topRole = roles.get(0);

            String roleName = topRole.get("role").toString();

            List<String> skillGaps =
                    (List<String>) topRole.get("top_missing_skills");

            LearningRoadmapResponse roadmap =
                    roadmapService.generateRoadmap(resumeId, roleName);

            List<JobRecommendation> jobs =
                    jobRecommendationService.getJobRecommendations(resumeId);

            return CareerReportResponse.builder()
                    .resumeId(resumeId)
                    .careerInsight(resume.getAnalysisResult().getCareerInsight())
                    .bestRoles(roleScores)
                    .skillGaps(skillGaps)
                    .learningRoadmap(roadmap.getRoadmap())
                    .recommendedJobs(jobs)
                    .build();

        } catch (Exception e) {

            throw new RuntimeException("Failed to generate career report", e);
        }
    }
}