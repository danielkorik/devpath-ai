package com.devpath.backend.dto;

import lombok.Data;
import java.util.List;


@Data
public class CareerSimulationRequest {

    private Long resumeId;

    private String role;

    private List<String> skillsToAdd;
}
