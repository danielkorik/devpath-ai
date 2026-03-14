package com.devpath.backend.config;

import com.devpath.backend.entity.Skill;
import com.devpath.backend.entity.SkillDependency;
import com.devpath.backend.repository.SkillDependencyRepository;
import com.devpath.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SkillDataInitializer implements CommandLineRunner {

    private final SkillRepository skillRepository;
    private final SkillDependencyRepository dependencyRepository;

    @Override
    public void run(String... args) {

        Skill linux = getOrCreateSkill("linux", "core");
        Skill docker = getOrCreateSkill("docker", "devops");
        Skill kubernetes = getOrCreateSkill("kubernetes", "devops");
        Skill aws = getOrCreateSkill("aws", "cloud");

        createDependency(docker, linux);
        createDependency(kubernetes, docker);
        createDependency(aws, docker);
    }

    private Skill getOrCreateSkill(String name, String category) {

        return skillRepository.findByNameIgnoreCase(name)
                .orElseGet(() ->
                        skillRepository.save(
                                Skill.builder()
                                        .name(name)
                                        .category(category)
                                        .build()
                        )
                );
    }

    private void createDependency(Skill skill, Skill dependsOn) {

        boolean exists = dependencyRepository
                .existsBySkillAndDependsOnSkill(skill, dependsOn);

        if (!exists) {

            dependencyRepository.save(
                    SkillDependency.builder()
                            .skill(skill)
                            .dependsOnSkill(dependsOn)
                            .build()
            );
        }
    }
}