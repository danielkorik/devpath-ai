package com.devpath.backend.controller;

import com.devpath.backend.dto.CareerSimulationRequest;
import com.devpath.backend.dto.CareerSimulationResponse;
import com.devpath.backend.service.CareerSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/career")
@RequiredArgsConstructor
public class CareerSimulationController {

    private final CareerSimulationService simulationService;

    @PostMapping("/simulate")
    public ResponseEntity<CareerSimulationResponse> simulateCareer(
            @RequestBody CareerSimulationRequest request
    ) {

        CareerSimulationResponse response =
                simulationService.simulateCareer(
                        request.getResumeId(),
                        request.getRole(),
                        request.getSkillsToAdd()
                );

        return ResponseEntity.ok(response);
    }
}