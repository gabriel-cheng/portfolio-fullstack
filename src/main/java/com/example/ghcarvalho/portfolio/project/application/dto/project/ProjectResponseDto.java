package com.example.ghcarvalho.portfolio.project.application.dto.project;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.example.ghcarvalho.portfolio.project.domain.project.ProjectStatus;

public record ProjectResponseDto(
        UUID id,
        String title,
        String slug,
        String summary,
        Map<String, Object> content,
        boolean featured,
        int displayOrder,
        ProjectStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}