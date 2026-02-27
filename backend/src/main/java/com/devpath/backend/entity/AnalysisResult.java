package com.devpath.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String skillsDetected;

    @Column(columnDefinition = "TEXT")
    private String improvementSuggestions;

    @OneToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;
}