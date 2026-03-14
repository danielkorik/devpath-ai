package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AutoApplyResponse {

    private int applicationsSent;

    private List<JobRecommendation> jobs;
}