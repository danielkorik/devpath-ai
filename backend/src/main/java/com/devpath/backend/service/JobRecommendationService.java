package com.devpath.backend.service;

import com.devpath.backend.dto.JobRecommendation;
import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.ResumeSkill;
import com.devpath.backend.repository.ResumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.devpath.backend.entity.ResumeSkill;
import java.util.stream.Collectors;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JobRecommendationService {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    public List<JobRecommendation> getJobRecommendations(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (resume.getAnalysisResult() == null)
            throw new RuntimeException("Resume not analyzed yet");

        try {

            Map<String, Object> analysis =
                    objectMapper.readValue(
                            resume.getAnalysisResult().getAnalysisJson(),
                            new TypeReference<Map<String, Object>>() {}
                    );

            List<Map<String, Object>> roles =
                    (List<Map<String, Object>>) analysis.get("best_fit_roles");

            Set<String> resumeSkills = getResumeSkills(resume);

            System.out.println("RESUME SKILLS:");
            System.out.println(resumeSkills);

            List<JobRecommendation> jobs = new ArrayList<>();

            for (int i = 0; i < Math.min(3, roles.size()); i++) {

                Map<String, Object> roleData = roles.get(i);

                String role = roleData.get("role").toString();

                List<String> requiredSkills =
                        (List<String>) roleData.get("matched_skills");

                System.out.println("ROLE REQUIRED SKILLS:");
                System.out.println(requiredSkills);

                List<String> missingSkills =
                        (List<String>) roleData.get("top_missing_skills");

                int matchScore = calculateMatchScore(resumeSkills, requiredSkills);

                jobs.addAll(searchJobs(role, matchScore, missingSkills));
            }

            return jobs;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch jobs", e);
        }
    }

    private Set<String> getResumeSkills(Resume resume) {

        if (resume.getResumeSkills() == null)
            return new HashSet<>();

        System.out.println("Resume Skills Loaded:");
        resume.getResumeSkills().forEach(rs ->
                System.out.println(rs.getSkill().getName())
        );

        return resume.getResumeSkills()
                .stream()
                .map(ResumeSkill::getSkill)
                .filter(Objects::nonNull)
                .map(skill -> skill.getName().toLowerCase().trim())
                .collect(Collectors.toSet());
    }

    private int calculateMatchScore(Set<String> resumeSkills, List<String> requiredSkills) {

        if (requiredSkills == null || requiredSkills.isEmpty())
            return 0;

        int matched = 0;

        for (String skill : requiredSkills) {

            String normalized = skill.toLowerCase().trim();

            if (resumeSkills.contains(normalized)) {
                matched++;
            }
        }

        System.out.println("MATCHED SKILLS: " + matched + "/" + requiredSkills.size());

        return (int) (((double) matched / requiredSkills.size()) * 100);
    }

    private List<JobRecommendation> searchJobs(String role, int matchScore, List<String> missingSkills) {

        role = role.replace("_", " ").toLowerCase();

        if (role.equals("systems engineer"))
            role = "system engineer";

        if (role.equals("cybersecurity engineer"))
            role = "cyber security";

        if (role.equals("devops engineer"))
            role = "devops engineer";

        String url =
                "https://jsearch.p.rapidapi.com/search?query=" +
                        role +
                        "&page=1&num_pages=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<JobRecommendation> jobs = new ArrayList<>();

        if (response.getBody() == null)
            return jobs;

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.getBody().get("data");

        if (data == null)
            return jobs;

        int limit = Math.min(3, data.size());

        for (int i = 0; i < limit; i++) {

            Map<String, Object> job = data.get(i);

            String title = (String) job.get("job_title");
            String company = (String) job.get("employer_name");

            String city = (String) job.get("job_city");
            String country = (String) job.get("job_country");

            String applyLink = (String) job.get("job_apply_link");

            if (city == null)
                city = "Remote";

            jobs.add(
                    JobRecommendation.builder()
                            .title(title)
                            .company(company)
                            .location(city + (country != null ? ", " + country : ""))
                            .applyLink(applyLink)
                            .matchScore(matchScore)
                            .missingSkills(missingSkills)
                            .build()
            );
        }

        return jobs;
    }
}