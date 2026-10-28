package com.example.ghcarvalho.portfolio.project.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank String username,
        @NotBlank String password
) {
}