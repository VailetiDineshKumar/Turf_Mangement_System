package com.example.turf.service;

import com.example.turf.dto.AuthResponse;
import com.example.turf.dto.LoginRequest;
import com.example.turf.dto.RegisterRequest;
import com.example.turf.entity.User;
import com.example.turf.enums.Role;
import com.example.turf.repository.UserRepository;
import com.example.turf.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // registration always creates a USER; admins are seeded separately
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
    }
}