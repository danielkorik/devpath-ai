package com.devpath.backend.controller;

import com.devpath.backend.dto.ResumeResponse;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.ResumeRepository;
import com.devpath.backend.repository.UserRepository;
import com.devpath.backend.service.ResumeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.devpath.backend.dto.RoleAnalysisResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ========================
    // Upload Resume
    // ========================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) throws IOException {
        resumeService.uploadResume(file);
        return ResponseEntity.ok("Resume uploaded and processed successfully");
    }

    // ========================
    // Get All User Resumes
    // ========================
    @GetMapping
    public List<ResumeResponse> getUserResumes() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return resumeRepository.findByUserId(user.getId())
                .stream()
                .map(resume -> ResumeResponse.builder()
                        .id(resume.getId())
                        .fileName(resume.getFileName())
                        .uploadedAt(resume.getUploadedAt())
                        .careerInsight(
                                resume.getAnalysisResult() != null
                                        ? resume.getAnalysisResult().getCareerInsight()
                                        : null
                        )
                        .analysis(
                                resume.getAnalysisResult() != null
                                        ? parseJson(resume.getAnalysisResult().getAnalysisJson())
                                        : null
                        )
                        .build()
                )
                .toList();
    }

    // ========================
    // Get Single Resume
    // ========================
    @GetMapping("/{id}")
    public ResumeResponse getResumeById(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return ResumeResponse.builder()
                .id(resume.getId())
                .fileName(resume.getFileName())
                .uploadedAt(resume.getUploadedAt())
                .careerInsight(
                        resume.getAnalysisResult() != null
                                ? resume.getAnalysisResult().getCareerInsight()
                                : null
                )
                .analysis(
                        resume.getAnalysisResult() != null
                                ? parseJson(resume.getAnalysisResult().getAnalysisJson())
                                : null
                )
                .build();
    }

    // ========================
    // Delete Resume
    // ========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if(!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        resumeRepository.delete(resume);

        return ResponseEntity.ok("Resume deleted successfully");

    }

    // ========================
    // Re-Analyze Resume
    // ========================

    @PostMapping("/{id}/reanalyze")
    public ResponseEntity<?> reanalyzeResume(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        resumeService.reanalyzeResume(resume);

        return ResponseEntity.ok("Resume reanalyzed successfully");
    }


    @GetMapping("/{resumeId}/roles/{roleName}")
    public ResponseEntity<RoleAnalysisResponse> getRoleAnalysis(
            @PathVariable Long resumeId,
            @PathVariable String roleName
    ) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Ensure resume belongs to logged-in user
        if (!resume.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        if (resume.getAnalysisResult() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Normalize role name to avoid case mismatch issues
        String normalizedRole = roleName.toLowerCase();

        RoleAnalysisResponse response =
                resumeService.getRoleAnalysis(resume, normalizedRole);

        return ResponseEntity.ok(response);
    }

    // ========================
    // JSON Parser Helper
    // ========================
    private Map<String, Object> parseJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse analysis JSON", e);
        }
    }
}