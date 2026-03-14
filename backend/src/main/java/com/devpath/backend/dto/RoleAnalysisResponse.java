package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RoleAnalysisResponse {

    private Long resumeId;
    private String role;
    private int score;
    private List<String> matchedSkills;
    private Map<String, List<String>> missingByCategory;
    private List<String> topMissingSkills;
    private String careerInsight;
    private List<SkillImpact> skillPriority;
}