package com.example.ghcarvalho.portfolio.project.application.dto.auth;

import com.example.ghcarvalho.portfolio.project.infra.persistence.user.AdminUserEntity;
import com.example.ghcarvalho.portfolio.project.infra.security.AuthenticatedUser;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String username,
        String role
) {
    public static UserResponseDTO from(AuthenticatedUser user) {
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("ADMIN");

        return new UserResponseDTO(user.getId(), user.getUsername(), role);
    }

    public static UserResponseDTO from(AdminUserEntity admin) {
        return new UserResponseDTO(admin.getId(), admin.getUsername(), "ADMIN");
    }
}
