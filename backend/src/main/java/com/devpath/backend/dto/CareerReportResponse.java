package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CareerReportResponse {

    private Long resumeId;

    private String careerInsight;

    private List<RoleScore> bestRoles;

    private List<String> skillGaps;

    private List<LearningStep> learningRoadmap;

    private List<JobRecommendation> recommendedJobs;

}