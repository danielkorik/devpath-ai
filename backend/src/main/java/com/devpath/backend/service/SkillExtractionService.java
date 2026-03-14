package com.devpath.backend.service;

import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.ResumeSkill;
import com.devpath.backend.entity.Skill;
import com.devpath.backend.repository.ResumeSkillRepository;
import com.devpath.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillExtractionService {

    private final SkillRepository skillRepository;
    private final ResumeSkillRepository resumeSkillRepository;

    public void extractSkills(String resumeText, Resume resume) {

        if (resume.getId() == null) {
            throw new RuntimeException("Resume must be saved before extracting skills");
        }

        List<Skill> skills = skillRepository.findAll();

        System.out.println("===== SKILL EXTRACTION START =====");

        String text = resumeText.toLowerCase();

        for (Skill skill : skills) {

            String skillName = skill.getName().toLowerCase();

            if (text.contains(skillName)) {

                boolean exists =
                        resumeSkillRepository.existsByResumeAndSkill(resume, skill);

                if (!exists) {

                    ResumeSkill resumeSkill = ResumeSkill.builder()
                            .resume(resume)
                            .skill(skill)
                            .build();

                    resumeSkillRepository.save(resumeSkill);

                    System.out.println("Detected skill: " + skill.getName());
                }
            }
        }

        System.out.println("===== SKILL EXTRACTION END =====");
    }
}