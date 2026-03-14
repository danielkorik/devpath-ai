package com.devpath.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobRecommendation {

    private String title;
    private String company;
    private String location;
    private String applyLink;

    private int matchScore;
    private List<String> missingSkills;
}
