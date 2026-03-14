package com.devpath.backend.controller;

import com.devpath.backend.dto.CareerReportResponse;
import com.devpath.backend.service.CareerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/career-report")
@RequiredArgsConstructor
public class CareerReportController {

    private final CareerReportService careerReportService;

    @GetMapping("/{resumeId}")
    public CareerReportResponse getCareerReport(
            @PathVariable Long resumeId
    ) {

        return careerReportService.generateReport(resumeId);
    }
}