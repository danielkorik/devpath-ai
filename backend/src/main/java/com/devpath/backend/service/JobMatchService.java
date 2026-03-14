package com.devpath.backend.service;

import com.devpath.backend.dto.JobMatchResponse;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.Skill;
import com.devpath.backend.repository.ResumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JobMatchService {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    public JobMatchResponse matchJob(Long resumeId, String role) {

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

            Map<String, Object> roleData = roles.stream()
                    .filter(r -> role.equals(r.get("role")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            int score = (int) roleData.get("score");

            List<String> matchedSkills =
                    (List<String>) roleData.get("matched_skills");

            List<String> missingSkills =
                    (List<String>) roleData.get("top_missing_skills");

            String insight = generateAdvice(score, missingSkills);

            return JobMatchResponse.builder()
                    .jobTitle(role.replace("_", " "))
                    .company("AI Job Market")
                    .matchScore(score)
                    .matchedSkills(matchedSkills)
                    .missingSkills(missingSkills)
                    .careerInsight(insight)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to match job", e);
        }
    }

    private String generateAdvice(int score, List<String> missingSkills) {

        if (score >= 85) {
            return "You are already a strong match for this role. Improving " +
                    missingSkills.get(0) + " could make you an outstanding candidate.";
        }

        if (score >= 70) {
            return "You are close to qualifying for this role. Focus on learning " +
                    String.join(", ", missingSkills.subList(0, Math.min(2, missingSkills.size()))) +
                    " to significantly improve your chances.";
        }

        return "This role requires additional preparation. Start by learning " +
                String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))) +
                " to build a stronger foundation.";
    }
}