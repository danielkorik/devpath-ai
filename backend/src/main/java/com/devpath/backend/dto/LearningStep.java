package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningStep {

    private int step;
    private String skill;
    private String category;
    private int estimatedDays;
}