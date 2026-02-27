package com.devpath.backend.service;

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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final RestTemplate restTemplate;
    private final AnalysisResultRepository analysisResultRepository;

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

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("resume_text", resumeText);

        Map response = restTemplate.postForObject(aiUrl, requestBody, Map.class);

        if (response == null) {
            throw new RuntimeException("AI service returned null response");
        }

        String summary = response.get("summary").toString();
        String skills = response.get("skills_detected").toString();
        String improvements = response.get("improvement_suggestions").toString();

        AnalysisResult result = AnalysisResult.builder()
                .summary(summary)
                .skillsDetected(skills)
                .improvementSuggestions(improvements)
                .resume(resume)
                .build();

        analysisResultRepository.save(result);
    }
}