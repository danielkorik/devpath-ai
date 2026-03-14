package com.devpath.backend.repository;

import com.devpath.backend.entity.Resume;
import com.devpath.backend.entity.ResumeSkill;
import com.devpath.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {

    List<ResumeSkill> findByResumeId(Long resumeId);
    boolean existsByResumeAndSkill(Resume resume, Skill skill);

}