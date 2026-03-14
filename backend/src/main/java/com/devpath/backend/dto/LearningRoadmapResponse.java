package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearningRoadmapResponse {

    private Long resumeId;
    private String targetRole;
    private int currentScore;
    private int estimatedImprovedScore;
    private List<LearningStep> roadmap;
}