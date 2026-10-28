package com.example.ghcarvalho.portfolio.project.application.dto.auth;

import java.util.UUID;

public record AuthenticatedUserDto(
        UUID id,
        String username,
        String role
) {
}