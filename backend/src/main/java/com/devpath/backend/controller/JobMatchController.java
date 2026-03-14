package com.devpath.backend.controller;

import com.devpath.backend.dto.JobMatchResponse;
import com.devpath.backend.service.JobMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-match")
@RequiredArgsConstructor
public class JobMatchController {

    private final JobMatchService jobMatchService;

    @GetMapping("/{resumeId}/{role}")
    public JobMatchResponse matchJob(
            @PathVariable Long resumeId,
            @PathVariable String role
    ) {

        return jobMatchService.matchJob(resumeId, role);
    }
}