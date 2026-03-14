package com.devpath.backend.repository;

import com.devpath.backend.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByResumeIdAndJobTitle(Long resumeId, String jobTitle);
}