package com.devpath.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CareerSimulationResponse {

    private String role;

    private int currentScore;

    private int simulatedScore;

    private int improvement;

    private List<String> skillsAdded;

}