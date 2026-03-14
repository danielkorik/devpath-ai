package com.devpath.backend.service;

import com.devpath.backend.entity.Skill;
import com.devpath.backend.entity.SkillDependency;
import com.devpath.backend.repository.SkillDependencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SkillGraphService {

    private final SkillDependencyRepository dependencyRepository;

    public List<Skill> buildLearningPath(Skill targetSkill) {

        List<Skill> orderedSkills = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        resolveDependencies(targetSkill, orderedSkills, visited);

        return orderedSkills;
    }

    private void resolveDependencies(
            Skill skill,
            List<Skill> orderedSkills,
            Set<Long> visited
    ) {

        if (visited.contains(skill.getId()))
            return;

        visited.add(skill.getId());

        List<SkillDependency> dependencies =
                dependencyRepository.findBySkill(skill);

        for (SkillDependency dep : dependencies) {

            resolveDependencies(dep.getDependsOnSkill(), orderedSkills, visited);
        }

        orderedSkills.add(skill);
    }
}