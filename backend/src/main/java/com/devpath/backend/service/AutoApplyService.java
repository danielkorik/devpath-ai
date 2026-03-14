package com.devpath.backend.service;

import com.devpath.backend.dto.AutoApplyResponse;
import com.devpath.backend.dto.JobRecommendation;
import com.devpath.backend.entity.JobApplication;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.repository.JobApplicationRepository;
import com.devpath.backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoApplyService {

    private final ResumeRepository resumeRepository;
    private final JobRecommendationService jobService;
    private final JobApplicationRepository applicationRepository;

    public AutoApplyResponse autoApply(Long resumeId, int minScore) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        List<JobRecommendation> jobs =
                jobService.getJobRecommendations(resumeId);

        List<JobRecommendation> appliedJobs = new ArrayList<>();

        for (JobRecommendation job : jobs) {

            if (job.getMatchScore() >= minScore &&
                    !applicationRepository.existsByResumeIdAndJobTitle(resume.getId(), job.getTitle())) {

                JobApplication application = JobApplication.builder()
                        .resume(resume)
                        .jobTitle(job.getTitle())
                        .company(job.getCompany())
                        .matchScore(job.getMatchScore())
                        .status("APPLIED")
                        .appliedAt(LocalDateTime.now())
                        .build();

                applicationRepository.save(application);

                appliedJobs.add(job);
            }
        }

        return AutoApplyResponse.builder()
                .applicationsSent(appliedJobs.size())
                .jobs(appliedJobs)
                .build();
    }
}