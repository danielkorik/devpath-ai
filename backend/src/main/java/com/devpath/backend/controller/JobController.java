package com.devpath.backend.controller;

import com.devpath.backend.dto.JobRecommendation;
import com.devpath.backend.service.JobRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobRecommendationService jobService;

    @GetMapping("/recommendations/{resumeId}")
    public List<JobRecommendation> getRecommendations(@PathVariable Long resumeId) {

        return jobService.getJobRecommendations(resumeId);
    }
}