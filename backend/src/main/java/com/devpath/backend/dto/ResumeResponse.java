package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ResumeResponse {

    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private Map<String, Object> analysis;
    private String careerInsight;
}