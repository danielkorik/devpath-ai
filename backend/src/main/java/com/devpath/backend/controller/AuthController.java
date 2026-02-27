package com.devpath.backend.controller;

import com.devpath.backend.dto.LoginRequest;
import com.devpath.backend.dto.RegisterRequest;
import com.devpath.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());

        if (token == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.ok(token);
    }
}