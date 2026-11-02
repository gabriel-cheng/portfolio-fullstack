package com.example.ghcarvalho.portfolio.project.application.dto.technology;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TechnologyResponseDto(
        UUID id,
        String name,
        String slug,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

