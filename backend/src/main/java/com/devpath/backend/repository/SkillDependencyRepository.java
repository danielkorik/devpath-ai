package com.devpath.backend.repository;

import com.devpath.backend.entity.Skill;
import com.devpath.backend.entity.SkillDependency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillDependencyRepository extends JpaRepository<SkillDependency, Long> {

    List<SkillDependency> findBySkill(Skill skill);
    boolean existsBySkillAndDependsOnSkill(Skill skill, Skill dependsOnSkill);

}