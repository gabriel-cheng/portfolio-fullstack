package com.example.ghcarvalho.portfolio.project.application.dto.company;

import com.example.ghcarvalho.portfolio.project.application.dto.experience.ExperienceResponseDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CompanyResponseDto(
        UUID id,
        String name,
        String slug,
        String description,
        String website,
        String logoUrl,
        List<ExperienceResponseDto> experiences,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

