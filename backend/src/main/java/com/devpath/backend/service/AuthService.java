package com.devpath.backend.service;

import com.devpath.backend.dto.RegisterRequest;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.UserRepository;
import com.devpath.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public String login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtService.generateToken(user.getEmail()))
                .orElse(null);
    }
}