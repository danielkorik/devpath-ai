package com.devpath.backend.service;

import com.devpath.backend.dto.CareerSimulationResponse;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.ResumeSkill;
import com.devpath.backend.repository.ResumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerSimulationService {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    public CareerSimulationResponse simulateCareer(
            Long resumeId,
            String role,
            List<String> skillsToAdd
    ) {

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

            int currentScore = (int) roleData.get("score");

            List<String> requiredSkills =
                    (List<String>) roleData.get("matched_skills");

            Set<String> resumeSkills = resume.getResumeSkills()
                    .stream()
                    .map(ResumeSkill::getSkill)
                    .map(skill -> skill.getName().toLowerCase())
                    .collect(Collectors.toSet());

            // add simulated skills
            resumeSkills.addAll(
                    skillsToAdd.stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet())
            );

            int matched = 0;

            for (String skill : requiredSkills) {

                if (resumeSkills.contains(skill.toLowerCase()))
                    matched++;
            }

            int simulatedScore =
                    (int)(((double)matched / requiredSkills.size()) * 100);

            simulatedScore = Math.min(simulatedScore, 95);

            return CareerSimulationResponse.builder()
                    .role(role)
                    .currentScore(currentScore)
                    .simulatedScore(simulatedScore)
                    .improvement(simulatedScore - currentScore)
                    .skillsAdded(skillsToAdd)
                    .build();

        } catch (Exception e) {

            throw new RuntimeException("Simulation failed", e);
        }
    }
}