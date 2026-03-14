package com.devpath.backend.service;

import com.devpath.backend.dto.RoleAnalysisResponse;
import com.devpath.backend.entity.AnalysisResult;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.AnalysisResultRepository;
import com.devpath.backend.repository.ResumeRepository;
import com.devpath.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.devpath.backend.dto.SkillImpact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final RestTemplate restTemplate;
    private final AnalysisResultRepository analysisResultRepository;
    private final ObjectMapper objectMapper;
    private final SkillExtractionService skillExtractionService;

    public void uploadResume(MultipartFile file) throws IOException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String savedPath = fileStorageService.saveFile(file);

        String extractedText = extractTextFromPdf(savedPath);

        Resume resume = Resume.builder()
                .fileName(file.getOriginalFilename())
                .filePath(savedPath)
                .extractedText(extractedText)
                .user(user)
                .build();

        resumeRepository.save(resume);

        skillExtractionService.extractSkills(extractedText, resume);

        //debug
        System.out.println("===== EXTRACTED TEXT START =====");
        System.out.println(extractedText);
        System.out.println("===== EXTRACTED TEXT END =====");
        System.out.println("Word count: " + extractedText.split("\\s+").length);

        System.out.println("Sending to AI word count: " + extractedText.split("\\s+").length);
        //---------------------

        callAiService(extractedText, resume);

    }

    private String extractTextFromPdf(String filePath) throws IOException {

        File file = new File(filePath);

        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private void callAiService(String resumeText, Resume resume) {

        String aiUrl = "http://localhost:8001/analyze";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("resume_text", resumeText);

        Map<String, Object> response =
                restTemplate.postForObject(aiUrl, requestBody, Map.class);

        if (response == null) {
            throw new RuntimeException("AI service returned null response");
        }

        try {
            String fullResponseJson = objectMapper.writeValueAsString(response);

            String careerInsight = generateInsight(response);

            AnalysisResult result = AnalysisResult.builder()
                    .analysisJson(fullResponseJson)
                    .careerInsight(careerInsight)
                    .resume(resume)
                    .build();

            resume.setAnalysisResult(result);

            analysisResultRepository.save(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize AI response", e);
        }
    }
    public void reanalyzeResume(Resume resume) {

        if (resume.getExtractedText() == null) {
            throw new RuntimeException("No extracted text found");
        }

        callAiService(resume.getExtractedText(), resume);
    }
    private String generateInsight(Map<String, Object> response) {

        List<Map<String, Object>> bestRoles =
                (List<Map<String, Object>>) response.get("best_fit_roles");

        if (bestRoles == null || bestRoles.isEmpty()) {
            return "No strong role match found. Consider expanding technical skills.";
        }

        Map<String, Object> topRole = bestRoles.get(0);

        String roleName = topRole.get("role").toString();
        int score = (int) topRole.get("score");

        List<String> missing =
                (List<String>) topRole.get("top_missing_skills");

        String missingText = "";

        if (missing != null && !missing.isEmpty()) {
            missingText = " To improve alignment, focus on: "
                    + String.join(", ", missing.subList(0, Math.min(3, missing.size())))
                    + ".";
        }

        return "Your strongest role match is " + roleName +
                " with a compatibility score of " + score + "%."
                + missingText;
    }

    @SuppressWarnings("unchecked")
    public RoleAnalysisResponse getRoleAnalysis(Resume resume, String roleName) {

        try {
            Map<String, Object> analysis =
                    objectMapper.readValue(
                            resume.getAnalysisResult().getAnalysisJson(),
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
                    );

            List<Map<String, Object>> bestRoles =
                    (List<Map<String, Object>>) analysis.get("best_fit_roles");

            Map<String, Object> roleData = bestRoles.stream()
                    .filter(r -> roleName.equals(r.get("role")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Role not found in analysis"));

            int score = (int) roleData.get("score");

            List<String> topMissing =
                    (List<String>) roleData.get("top_missing_skills");

            Map<String, List<String>> missingByCategory =
                    (Map<String, List<String>>) roleData.get("missing_by_category");

            // ⭐ Skill impact calculation
            List<SkillImpact> skillPriority =
                    calculateSkillImpact(missingByCategory);

            String insight;

            if (score >= 80) {
                if (topMissing != null && !topMissing.isEmpty()) {
                    insight = "You're highly aligned with " + roleName +
                            " (" + score + "%). Adding " + topMissing.get(0) +
                            " would likely push you close to full alignment and make you highly competitive.";
                } else {
                    insight = "You're strongly aligned with " + roleName +
                            ". Your profile is already competitive.";
                }

            } else if (score >= 60) {
                insight = "You have a solid foundation for " + roleName +
                        " (" + score + "%). Prioritize improving: " +
                        String.join(", ", topMissing.subList(0, Math.min(2, topMissing.size()))) +
                        " to increase alignment significantly.";

            } else {
                insight = "You currently match " + score + "% of " + roleName +
                        " requirements. Focus on building core missing skills before targeting this role.";
            }

            return RoleAnalysisResponse.builder()
                    .resumeId(resume.getId())
                    .role(roleName)
                    .score(score)
                    .matchedSkills((List<String>) roleData.get("matched_skills"))
                    .missingByCategory(missingByCategory)
                    .topMissingSkills(topMissing)
                    .skillPriority(skillPriority) // ⭐ NEW
                    .careerInsight(insight)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse role analysis", e);
        }
    }


    private List<SkillImpact> calculateSkillImpact(Map<String, List<String>> missingByCategory) {

        Map<String, Integer> categoryWeights = Map.of(
                "core", 15,
                "frameworks", 12,
                "systems", 12,
                "debugging", 10,
                "cloud", 9,
                "devops", 8,
                "database", 8,
                "tools", 6
        );

        List<SkillImpact> impacts = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : missingByCategory.entrySet()) {

            String category = entry.getKey();
            List<String> skills = entry.getValue();

            int weight = categoryWeights.getOrDefault(category, 5);

            for (String skill : skills) {

                impacts.add(
                        SkillImpact.builder()
                                .skill(skill)
                                .impactScore(weight)
                                .category(category)
                                .build()
                );
            }
        }

        impacts.sort((a, b) -> Integer.compare(b.getImpactScore(), a.getImpactScore()));

        return impacts;
    }
}