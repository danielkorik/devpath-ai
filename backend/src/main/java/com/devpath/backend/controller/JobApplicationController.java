package com.devpath.backend.controller;

import com.devpath.backend.dto.AutoApplyResponse;
import com.devpath.backend.service.AutoApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobApplicationController {

    private final AutoApplyService autoApplyService;

    @PostMapping("/auto-apply")
    public ResponseEntity<AutoApplyResponse> autoApply(
            @RequestParam Long resumeId,
            @RequestParam(defaultValue = "70") int minScore
    ) {

        AutoApplyResponse response =
                autoApplyService.autoApply(resumeId, minScore);

        return ResponseEntity.ok(response);
    }
}