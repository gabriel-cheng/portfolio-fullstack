package com.example.ghcarvalho.portfolio.project.application.dto.education;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EducationResponseDto(
        UUID id,
        String degree,
        String institution,
        String startYear,
        String endYear,
        String status,
        String type,
        String description,
        List<String> skills,
        String diplomaImage,
        String diplomaAlt,
        int displayOrder,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

