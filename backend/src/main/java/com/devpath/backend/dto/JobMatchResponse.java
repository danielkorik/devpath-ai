package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobMatchResponse {

    private String jobTitle;
    private String company;

    private int matchScore;

    private List<String> matchedSkills;
    private List<String> missingSkills;

    private String careerInsight;
}