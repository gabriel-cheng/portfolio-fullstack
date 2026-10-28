package com.example.ghcarvalho.portfolio.project.application.dto.experience;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.ghcarvalho.portfolio.project.domain.experience.EmploymentType;
import com.example.ghcarvalho.portfolio.project.domain.experience.ExperienceStatus;

public record ExperienceResponseDto(
        UUID id,
        String slug,
        String company,
        String role,
        EmploymentType employmentType,
        String location,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        boolean current,
        int displayOrder,
        ExperienceStatus status,
        List<String> technologies,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}