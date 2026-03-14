package com.devpath.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;

    private String company;

    private int matchScore;

    private String status;

    private LocalDateTime appliedAt;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;
}