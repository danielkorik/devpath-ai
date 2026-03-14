package com.devpath.backend.controller;

import com.devpath.backend.dto.LearningRoadmapResponse;
import com.devpath.backend.service.LearningRoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final LearningRoadmapService roadmapService;

    @GetMapping("/{resumeId}/{role}")
    public LearningRoadmapResponse generateRoadmap(
            @PathVariable Long resumeId,
            @PathVariable String role
    ) {

        return roadmapService.generateRoadmap(resumeId, role);
    }
}