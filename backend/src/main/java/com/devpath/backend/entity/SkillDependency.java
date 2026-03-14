package com.devpath.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skill_dependency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "depends_on_skill_id")
    private Skill dependsOnSkill;
}