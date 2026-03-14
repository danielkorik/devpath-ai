package com.devpath.backend.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillImpact {
    private String skill;
    private int impactScore;
    private String category;
}
