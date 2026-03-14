package com.devpath.backend.service;

import com.devpath.backend.dto.LearningRoadmapResponse;
import com.devpath.backend.dto.LearningStep;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.Skill;
import com.devpath.backend.entity.SkillDependency;
import com.devpath.backend.repository.ResumeRepository;
import com.devpath.backend.repository.SkillRepository;
import com.devpath.backend.repository.SkillDependencyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LearningRoadmapService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final SkillDependencyRepository dependencyRepository;
    private final ObjectMapper objectMapper;
    private final SkillGraphService skillGraphService;


    public LearningRoadmapResponse generateRoadmap(Long resumeId, String role) {

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

            List<String> missingSkills =
                    (List<String>) roleData.get("top_missing_skills");

            if (missingSkills == null || missingSkills.isEmpty()) {
                return LearningRoadmapResponse.builder()
                        .resumeId(resumeId)
                        .targetRole(role)
                        .currentScore(score)
                        .estimatedImprovedScore(score)
                        .roadmap(Collections.emptyList())
                        .build();
            }

            List<LearningStep> roadmap = new ArrayList<>();
            Set<String> addedSkills = new HashSet<>();

            int step = 1;

            for (String skillName : missingSkills) {

                Optional<Skill> skillOpt =
                        skillRepository.findByNameIgnoreCase(skillName);

                if (skillOpt.isEmpty()) continue;

                Skill skill = skillOpt.get();

                List<Skill> orderedSkills =
                        skillGraphService.buildLearningPath(skill);

                for (Skill s : orderedSkills) {

                    if (addedSkills.contains(s.getName())) continue;

                    int days = estimateDays(s.getCategory());

                    roadmap.add(
                            LearningStep.builder()
                                    .step(step++)
                                    .skill(s.getName())
                                    .category(s.getCategory())
                                    .estimatedDays(days)
                                    .build()
                    );

                    addedSkills.add(s.getName());
                }
            }

            return LearningRoadmapResponse.builder()
                    .resumeId(resumeId)
                    .targetRole(role)
                    .currentScore(score)
                    .estimatedImprovedScore(Math.min(score + 15, 100))
                    .roadmap(roadmap)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate roadmap", e);
        }
    }

    private List<Skill> resolveDependencies(Skill skill, Set<Long> visited) {

        List<Skill> ordered = new ArrayList<>();

        if (visited.contains(skill.getId())) {
            return ordered;
        }

        visited.add(skill.getId());

        List<SkillDependency> deps =
                dependencyRepository.findBySkill(skill);

        for (SkillDependency dep : deps) {

            Skill dependency = dep.getDependsOnSkill();

            ordered.addAll(resolveDependencies(dependency, visited));

            ordered.add(dependency);
        }

        ordered.add(skill);

        return ordered;
    }

    private int estimateDays(String category) {

        if (category == null) return 4;

        return switch (category) {
            case "core" -> 7;
            case "cloud" -> 6;
            case "devops" -> 6;
            case "debugging" -> 3;
            case "database" -> 5;
            default -> 4;
        };
    }
}