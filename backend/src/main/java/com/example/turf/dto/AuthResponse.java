package com.example.turf.dto;

import com.example.turf.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private Role role;
}